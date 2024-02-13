package com.hexacore.tayo.car;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.dto.GetSubCategoryResponseDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.dto.GetSubCategoryListResponseDto;
import com.hexacore.tayo.car.dto.GetDateListRequestDto;
import com.hexacore.tayo.car.model.Image;
import com.hexacore.tayo.car.dto.CreatePositionRequestDto;
import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.model.SubCategory;
import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.image.S3Manager;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final S3Manager s3Manager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* 차량 등록 */
    @Transactional
    public ResponseDto createCar(CreateCarRequestDto createCarRequestDto, Long userId) {
        if (checkUserHasCar(userId)) {
            // 유저가 이미 차량을 등록한 경우
            throw new GeneralException(ErrorCode.USER_ALREADY_HAS_CAR);
        }
        if (checkDuplicateCarNumber(createCarRequestDto.getCarNumber())) {
            // 중복되는 차량 번호가 있을 경우
            throw new GeneralException(ErrorCode.CAR_NUMBER_DUPLICATED);
        }
        if (!isIndexSizeEqualsToImageSize(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles())) {
            // index 리스트 길이와 image 리스트 길이가 같지 않은 경우
            throw new GeneralException(ErrorCode.IMAGE_INDEX_MISMATCH);
        }

        // 등록에 필요한 정보 가져오기
        SubCategory subCategory = subCategoryRepository.findByName(createCarRequestDto.getCarName())
                // 존재하지 않는 모델인 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_MODEL_NOT_FOUND));
        Point position = createPoint(createCarRequestDto.getPosition());

        Car car = carRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(userId != null ? userId : 1L,
                createCarRequestDto.getCarNumber())
                .orElse(null);

        if (car != null) {
            // 유저가 이전에 등록한 같은 번호의 차가 있는 경우 UPDATE
            car.setSubCategory(subCategory);
            car.setMileage(createCarRequestDto.getMileage());
            car.setFuel(createCarRequestDto.getFuel());
            car.setType(CarType.of(createCarRequestDto.getType()));
            car.setCapacity(createCarRequestDto.getCapacity());
            car.setYear(createCarRequestDto.getYear());
            car.setFeePerHour(createCarRequestDto.getFeePerHour());
            car.setAddress(createCarRequestDto.getAddress());
            car.setPosition(position);
            car.setDescription(createCarRequestDto.getDescription());
            car.setIsDeleted(false);
            // 이미지 저장
            saveImages(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles(), car);
        } else {
            // 유저가 이전에 등록한 같은 번호의 차가 없는 경우 CREATE
            Car carEntity = Car.builder()
                    .owner(User.builder().id(userId != null ? userId : 1L).build())
                    .subCategory(subCategory)
                    .carNumber(createCarRequestDto.getCarNumber())
                    .mileage(createCarRequestDto.getMileage())
                    .fuel(createCarRequestDto.getFuel())
                    .type(CarType.of(createCarRequestDto.getType()))
                    .capacity(createCarRequestDto.getCapacity())
                    .year(createCarRequestDto.getYear())
                    .feePerHour(createCarRequestDto.getFeePerHour())
                    .address(createCarRequestDto.getAddress())
                    .position(createPoint(createCarRequestDto.getPosition()))
                    .description(createCarRequestDto.getDescription())
                    .build();

            carRepository.save(carEntity);
            // 이미지 저장
            saveImages(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles(), carEntity);
        }

        return ResponseDto.success(HttpStatus.CREATED);
    }

    /* 차량 정보 조회 */
    public DataResponseDto carDetail(Long carId) {
        Car car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        List<String> images = carDateList(carId);
        return DataResponseDto.of(new GetCarResponseDto(car, images));
    }

    /* 차량 정보 수정 */
    @Transactional
    public ResponseDto carUpdate(Long carId, UpdateCarRequestDto updateCarRequestDto) {
        Car car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        car.setFeePerHour(updateCarRequestDto.getFeePerHour());
        car.setAddress(updateCarRequestDto.getAddress());
        car.setPosition(updateCarRequestDto.getPosition().toEntity());
        car.setDescription(updateCarRequestDto.getDescription());

        saveImages(updateCarRequestDto.getImageIndexes(), updateCarRequestDto.getImageFiles(), car);

        return ResponseDto.success(HttpStatus.OK);
    }

    /* 차량 삭제 */
    @Transactional
    public ResponseDto deleteCar(Long carId) {
        // 차량 isDeleted = true
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        car.setIsDeleted(true);
        carRepository.save(car);

        // 이미지 isDeleted = true
        imageRepository.findByCar_Id(car.getId()).forEach((image) -> {
            image.setIsDeleted(true);
            imageRepository.save(image);
        });

        return ResponseDto.success(HttpStatus.NO_CONTENT);
    }

    /* 에약 가능 날짜 조회 */
    private List<String> carDateList(Long carId) {
        return imageRepository.findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(carId)
                .stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }

    /* 예약 가능 날짜 수정 */
    public ResponseDto updateDates(Long carId, DateListDto dateList) {
        // 차량 조회가 안 되는 경우
        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        // dateListDto의 각 구간이 [시작, 끝] 으로 이루어지지 않거나 시작 날짜가 끝 날짜보다 뒤에 있는 경우
        for (List<Date> dates : dateList.getDates()) {
            if (dates.size() != 2) {
                throw new GeneralException(ErrorCode.DATE_SIZE_MISMATCH);
            }

            if (dates.get(0).after(dates.get(1))) {
                throw new GeneralException(ErrorCode.START_DATE_AFTER_END_DATE);
            }
        }

        car.setDates(dateList.getDates());
        carRepository.save(car);

        return ResponseDto.success(HttpStatus.ACCEPTED);
    }

    /* 모델, 세부 모델명 조회 */
    public ResponseDto getSubCategories() {
        List<GetSubCategoryResponseDto> models = subCategoryRepository.findAll().stream()
                .map(subCategory -> new GetSubCategoryResponseDto(subCategory.getName()))
                .toList();
        return DataResponseDto.of(new GetSubCategoryListResponseDto(models));
    }

    /* 경도와 위도 값을 Point 객체로 변환 */
    private Point createPoint(CreatePositionRequestDto createPositionRequestDto) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(createPositionRequestDto.getLng(), createPositionRequestDto.getLat());
        return geometryFactory.createPoint(coordinate);
    }

    /* 이미지 엔티티 저장 */
    private void saveImages(List<Integer> indexes, List<MultipartFile> files, Car car) {
        if (!imageRepository.existsByCar_Id(car.getId()) && indexes.size() < 5) {
            throw new GeneralException(ErrorCode.CAR_IMAGE_INSUFFICIENT);
        }
        List<Map<String, Object>> datas = IntStream.range(0, Math.min(indexes.size(), files.size()))
                .mapToObj(i -> {
                    String url = s3Manager.uploadImage(files.get(i));
                    Object index = indexes.get(i);
                    return Map.of("index", index, "url", url);
                })
                .toList();

        for (Map<String, Object> data : datas) {
            int idx = (int) data.get("index");
            String url = (String) data.get("url");

            Optional<Image> optionalImage = imageRepository.findByCar_IdAndOrderIdxAndIsDeletedFalse(
                    car.getId(), idx);
            Image image;
            // 인덱스가 idx인 image가 존재하면 soft delete
            if (optionalImage.isPresent()) {
                image = optionalImage.get();
                image.setIsDeleted(true);
                imageRepository.save(image);
            }
            // 새로 만들어서 추가하기
            image = Image.builder()
                    .car(car)
                    .url(url)
                    .orderIdx(idx)
                    .build();

            imageRepository.save(image);
        }
    }

    /* 유저가 등록한 차량이 있는지 체크 */
    private Boolean checkUserHasCar(Long userId) {
        return !carRepository.findByOwner_IdAndIsDeletedFalse(userId).isEmpty();
    }

    /* 중복된 차량 번호가 있는지 체크 */
    private Boolean checkDuplicateCarNumber(String carNumber) {
        return !carRepository.findByCarNumberAndIsDeletedFalse(carNumber).isEmpty();
    }

    private Boolean isIndexSizeEqualsToImageSize(List<Integer> imageIndexes, List<MultipartFile> imageFiles) {
        return imageIndexes.size() == imageFiles.size();
    }
}
