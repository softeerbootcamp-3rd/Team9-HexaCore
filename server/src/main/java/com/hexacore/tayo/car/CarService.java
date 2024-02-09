package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.*;
import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hexacore.tayo.user.model.UserEntity;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ImageRepository imageRepository;

    /* 차량 정보 조회 */
    public DataResponseDto carDetail(Long carId) {
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ResponseCode.NOT_FOUND, "존재하지 않는 id입니다"));
        List<String> images = carDateList(carId);
        return DataResponseDto.of(new CarDto(car,images));
    }

    /* 차량 정보 수정 */
    public ResponseDto carUpdate(Long carId, CarUpdateDto carUpdateDto){
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ResponseCode.NOT_FOUND, "존재하지 않는 id입니다"));
        car.setFeePerHour(carUpdateDto.getFeePerHour());
        car.setAddress(carUpdateDto.getAddress());
        car.setPosition(carUpdateDto.getPosition().toEntity());
        car.setDescription(carUpdateDto.getDescription());
        
       //updateImage(carId,carUpdateDto.getImageUrls());

        return ResponseDto.success(ResponseCode.OK, "차량 수정이 완료되었습니다.");
    }

    /* 에약 가능 날짜 조회 */
    private List<String> carDateList(Long carId){
        return imageRepository.findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(carId)
                .stream()
                .map(ImageEntity::getUrl)
                .collect(Collectors.toList());
    private final ModelRepository modelRepository;
    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* 모델, 세부 모델명 조회 */
    public ResponseDto getCategories() {
        List<CategoryDto> models = modelRepository.findAll().stream()
                .map(CategoryDto::new)
                .toList();
        return DataResponseDto.of(new CategoryListDto(models));
    }

    /* 차량 등록 */
    @Transactional
    public ResponseDto createCar(PostCarDto postCarDto) {
        // TODO: JWT 토큰에서 userId 가져와서 로그인한 경우에만 실행되도록
        Long userId = 1L;

        if (checkUserHasCar(userId)) {
            // 유저가 이미 차량을 등록한 경우
            throw new GeneralException(ErrorCode.USER_ALREADY_HAS_CAR);
        }
        if (checkDuplicateCarNumber(postCarDto.getCarNumber())) {
            // 중복되는 차량 번호가 있을 경우
            throw new GeneralException(ErrorCode.CAR_NUMBER_DUPLICATED);
        }

        // 등록에 필요한 정보 가져오기
        ModelEntity model = modelRepository.findBySubCategory(postCarDto.getCarName())
                // 존재하지 않는 모델인 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_MODEL_NOT_FOUND));
        Point position = createPoint(postCarDto.getPosition());

        CarEntity car = carRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(userId, postCarDto.getCarNumber())
                .orElse(null);

        if (car != null) {
            // 유저가 이전에 등록한 같은 번호의 차가 있는 경우 UPDATE
            car.setModel(model);
            car.setMileage(postCarDto.getMileage());
            car.setFuel(postCarDto.getFuel());
            car.setType(CarType.of(postCarDto.getType()));
            car.setCapacity(postCarDto.getCapacity());
            car.setYear(postCarDto.getYear());
            car.setFeePerHour(postCarDto.getFeePerHour());
            car.setAddress(postCarDto.getAddress());
            car.setPosition(position);
            car.setDescription(postCarDto.getDescription());
            car.setIsDeleted(false);
            // 이미지 저장
            saveImages(postCarDto.getImages(), car);
        } else {
            // 유저가 이전에 등록한 같은 번호의 차가 없는 경우 CREATE
            CarEntity carEntity = CarEntity.builder()
                    .owner(UserEntity.builder().id(userId).build())
                    .model(model)
                    .carNumber(postCarDto.getCarNumber())
                    .mileage(postCarDto.getMileage())
                    .fuel(postCarDto.getFuel())
                    .type(CarType.of(postCarDto.getType()))
                    .capacity(postCarDto.getCapacity())
                    .year(postCarDto.getYear())
                    .feePerHour(postCarDto.getFeePerHour())
                    .address(postCarDto.getAddress())
                    .position(createPoint(postCarDto.getPosition()))
                    .description(postCarDto.getDescription())
                    .build();

            carRepository.save(carEntity);
            // 이미지 저장
            saveImages(postCarDto.getImages(), carEntity);
        }

        return ResponseDto.success(HttpStatus.CREATED);
    }

    /* 경도와 위도 값을 Point 객체로 변환 */
    private Point createPoint(PositionDto positionDto) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(positionDto.getLng(), positionDto.getLat());
        return geometryFactory.createPoint(coordinate);
    }

    /* 이미지 엔티티 저장 */
    private void saveImages(List<MultipartFile> images, CarEntity carEntity) {
        List<String> urls = images.stream()
                .map(this::uploadImage)
                .toList();

        if (urls.size() < 5) {
            throw new GeneralException(ErrorCode.CAR_IMAGE_INSUFFICIENT);
        }

        for (int idx = 0; idx < urls.size(); idx++) {
            String url = urls.get(idx);
            ImageEntity imageEntity = ImageEntity.builder()
                    .car(carEntity)
                    .url(url)
                    .orderIdx(idx)
                    .build();
            imageRepository.save(imageEntity);
        }
    }

    /* 이미지 파일을 S3에 업로드하고 URL 반환 */
    private String uploadImage(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, originalFilename, image.getInputStream(), metadata)
            );
        } catch (IOException e) {
            throw new GeneralException(ErrorCode.S3_UPLOAD_FAILED);
        }
        return amazonS3Client.getUrl(bucket, originalFilename).toString();
    }

    /* 유저가 등록한 차량이 있는지 체크 */
    private Boolean checkUserHasCar(Long userId) {
        return !carRepository.findByOwner_IdAndIsDeletedFalse(userId).isEmpty();
    }

    /* 중복된 차량 번호가 있는지 체크 */
    private Boolean checkDuplicateCarNumber(String carNumber) {
        return !carRepository.findByCarNumberAndIsDeletedFalse(carNumber).isEmpty();
    }

    /* 차량 삭제 */
    @Transactional
    public ResponseDto deleteCar(Long carId) {
        // 차량 isDeleted = true
        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        car.setIsDeleted(true);
        carRepository.save(car);

        // 이미지 isDeleted = true
        imageRepository.findByCar_Id(car.getId()).forEach((image) -> {
            image.setIsDeleted(true);
            imageRepository.save(image);
        });

        return ResponseDto.success(HttpStatus.OK);
    }

    /* 예약 가능 날짜 수정 */
    public ResponseDto updateDates(Long carId, DateListDto dateListDto) {
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        car.setDates(dateListDto.getDates());
        carRepository.save(car);

        return ResponseDto.success(HttpStatus.ACCEPTED);
    }

    /* 차량 이미지 조회 */
    private void updateImage(Long carId, List<String> images){
        List<ImageEntity> carImages = imageRepository.findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(carId);
        for (int i = 0;i<carImages.size();i++) {
            ImageEntity carImage = carImages.get(i);
            String url = images.get(i);
            if(!carImage.getUrl().equals(url)){
                imageRepository.updateUrlById(carImage.getId(),url);
            }
        }
    }
}
