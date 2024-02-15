package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.dto.SearchCarsParamsDto;
import com.hexacore.tayo.car.dto.UpdateCarDateRangeRequestDto.CarDateRangeDto;
import com.hexacore.tayo.car.dto.UpdateCarDateRangeRequestDto.CarDateRangesDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.model.*;
import com.hexacore.tayo.category.SubcategoryRepository;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.user.model.User;
import com.hexacore.tayo.util.S3Manager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final CarDateRangeRepository carDateRangeRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final S3Manager s3Manager;

    /* 차량 등록 */
    @Transactional
    public void createCar(CreateCarRequestDto createCarRequestDto, Long userId) {
        if (isUserHavingCar(userId)) {
            // 유저가 이미 차량을 등록한 경우
            throw new GeneralException(ErrorCode.USER_ALREADY_HAS_CAR);
        }
        if (isCarNumberDuplicated(createCarRequestDto.getCarNumber())) {
            // 중복되는 차량 번호가 있을 경우
            throw new GeneralException(ErrorCode.CAR_NUMBER_DUPLICATED);
        }
        if (!isSupportedCarType(createCarRequestDto.getType())) {
            // 지원하는 않는 차량 타입인 경우
            throw new GeneralException(ErrorCode.INVALID_CAR_TYPE);
        }
        if (!isSupportedFuelType(createCarRequestDto.getFuel())) {
            // 지원하지 않는 연료 타입인 경우
            throw new GeneralException(ErrorCode.INVALID_FUEL_TYPE);
        }
        if (!isIndexSizeEqualsToImageSize(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles())) {
            // index 리스트 길이와 image 리스트 길이가 같지 않은 경우
            throw new GeneralException(ErrorCode.IMAGE_INDEX_MISMATCH);
        }

        // 등록에 필요한 정보 가져오기
        Subcategory subcategory = subcategoryRepository.findByName(createCarRequestDto.getCarName())
                // 존재하지 않는 모델인 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_MODEL_NOT_FOUND));

        Car car = carRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(userId, createCarRequestDto.getCarNumber())
                .orElse(null);

        if (car != null) {
            // 유저가 이전에 등록한 같은 번호의 차가 있는 경우 UPDATE
            car.setSubcategory(subcategory);
            car.setMileage(createCarRequestDto.getMileage());
            car.setFuel(FuelType.of(createCarRequestDto.getFuel()));
            car.setType(CarType.of(createCarRequestDto.getType()));
            car.setCapacity(createCarRequestDto.getCapacity());
            car.setYear(createCarRequestDto.getYear());
            car.setFeePerHour(createCarRequestDto.getFeePerHour());
            car.setAddress(createCarRequestDto.getAddress());
            car.setPosition(createCarRequestDto.getPosition().toPoint());
            car.setDescription(createCarRequestDto.getDescription());
            car.setIsDeleted(false);
            // 이미지 저장
            saveImages(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles(), car);
        } else {
            // 유저가 이전에 등록한 같은 번호의 차가 없는 경우 CREATE
            Car carEntity = Car.builder()
                    .owner(User.builder().id(userId).build())
                    .subcategory(subcategory)
                    .carNumber(createCarRequestDto.getCarNumber())
                    .mileage(createCarRequestDto.getMileage())
                    .fuel(FuelType.of(createCarRequestDto.getFuel()))
                    .type(CarType.of(createCarRequestDto.getType()))
                    .capacity(createCarRequestDto.getCapacity())
                    .year(createCarRequestDto.getYear())
                    .feePerHour(createCarRequestDto.getFeePerHour())
                    .address(createCarRequestDto.getAddress())
                    .position(createCarRequestDto.getPosition().toPoint())
                    .description(createCarRequestDto.getDescription())
                    .build();

            carRepository.save(carEntity);
            // 이미지 저장
            saveImages(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles(), carEntity);
        }
    }

    public Page<Car> searchCars(SearchCarsParamsDto searchCarsParamsDto, Pageable pageable) {
        Specification<Car> searchSpec = CarSpecifications.searchCars(searchCarsParamsDto);
        return carRepository.findAll(searchSpec, pageable);
    }

    /* 차량 정보 조회 */
    public GetCarResponseDto carDetail(Long carId) {
        Car car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        return GetCarResponseDto.of(car);
    }

    /* 차량 정보 수정 */
    @Transactional
    public void updateCar(Long carId, UpdateCarRequestDto updateCarRequestDto, Long userId) {
        if (!isIndexSizeEqualsToImageSize(updateCarRequestDto.getImageIndexes(), updateCarRequestDto.getImageFiles())) {
            // index 리스트 길이와 image 리스트 길이가 같지 않은 경우
            throw new GeneralException(ErrorCode.IMAGE_INDEX_MISMATCH);
        }
        Car car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        if (!car.getOwner().getId().equals(userId)) {
            throw new GeneralException(ErrorCode.CAR_UPDATED_BY_OTHERS);
        }
        car.setFeePerHour(updateCarRequestDto.getFeePerHour());
        car.setAddress(updateCarRequestDto.getAddress());
        car.setPosition(updateCarRequestDto.getPosition().toPoint());
        car.setDescription(updateCarRequestDto.getDescription());
        saveImages(updateCarRequestDto.getImageIndexes(), updateCarRequestDto.getImageFiles(), car);
        carRepository.save(car);
    }

    /* 차량 삭제 */
    @Transactional
    public void deleteCar(Long carId) {
        // 차량 isDeleted = true
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        car.setIsDeleted(true);
        carRepository.save(car);

        // 이미지 isDeleted = true
        carImageRepository.findByCar_Id(car.getId()).forEach((image) -> {
            image.setIsDeleted(true);
            carImageRepository.save(image);
        });
    }

    /* 예약 가능 날짜 수정 */
    @Transactional
    public void updateDateRanges(Long hostUserId, Long carId,
            CarDateRangesDto carDateRangesDto) {
        // 차량 조회가 안 되는 경우
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        // 차량 소유자와 일치하지 않을 경우
        if (!car.getOwner().getId().equals(hostUserId)) {
            throw new GeneralException(ErrorCode.CAR_DATE_RANGE_UPDATED_BY_OTHERS);
        }

        // dateListDto의 각 구간이 [시작, 끝] 으로 이루어지지 않거나 시작 날짜가 끝 날짜보다 뒤에 있는 경우
        for (List<LocalDate> carDateRangeList : carDateRangesDto.getDates()) {
            CarDateRangeDto carDateRangeDto = new CarDateRangeDto(carDateRangeList);

            LocalDate startDate = carDateRangeDto.getStartDate();
            LocalDate endDate = carDateRangeDto.getEndDate();

            if (startDate.isAfter(endDate)) {
                throw new GeneralException(ErrorCode.START_DATE_AFTER_END_DATE);
            }

            CarDateRange carDateRange = carDateRangeDto.toEntity(car);
            carDateRangeRepository.save(carDateRange);
        }
    }

    /* 이미지 엔티티 저장 */
    private void saveImages(List<Integer> indexes, List<MultipartFile> files, Car car) {
        if (!carImageRepository.existsByCar_Id(car.getId()) && indexes.size() < 5) {
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

            Optional<CarImage> optionalImage = carImageRepository.findByCar_IdAndOrderIdxAndIsDeletedFalse(
                    car.getId(), idx);
            CarImage carImage;
            // 인덱스가 idx인 image가 존재하면 soft delete
            if (optionalImage.isPresent()) {
                carImage = optionalImage.get();
                carImage.setIsDeleted(true);
                carImageRepository.save(carImage);
            }
            // 새로 만들어서 추가하기
            carImage = CarImage.builder()
                    .car(car)
                    .url(url)
                    .orderIdx(idx)
                    .build();

            carImageRepository.save(carImage);
        }
    }

    /* 유저가 등록한 차량이 있는지 체크 */
    private Boolean isUserHavingCar(Long userId) {
        return !carRepository.findByOwner_IdAndIsDeletedFalse(userId).isEmpty();
    }

    /* 중복된 차량 번호가 있는지 체크 */
    private Boolean isCarNumberDuplicated(String carNumber) {
        return !carRepository.findByCarNumberAndIsDeletedFalse(carNumber).isEmpty();
    }

    /* 인덱스 리스트와 이미지 리스트의 사이즈가 같은지 체크 */
    private Boolean isIndexSizeEqualsToImageSize(List<Integer> imageIndexes, List<MultipartFile> imageFiles) {
        return imageIndexes.size() == imageFiles.size();
    }

    /* CarType이 지원하는 형식인지 체크 */
    private Boolean isSupportedCarType(String carType) {
        return CarType.of(carType) != CarType.NOT_FOUND;
    }

    /* FuelType이 지원하는 형식인지 체크 */
    private Boolean isSupportedFuelType(String fuelType) {
        return FuelType.of(fuelType) != FuelType.NOT_FOUND;
    }
}
