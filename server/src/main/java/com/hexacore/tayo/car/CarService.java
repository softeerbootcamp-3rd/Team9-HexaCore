package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.dto.SearchCarsResultDto;
import com.hexacore.tayo.car.dto.UpdateCarDateRangeRequestDto.CarDateRangeDto;
import com.hexacore.tayo.car.dto.UpdateCarDateRangeRequestDto.CarDateRangesDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.FuelType;
import com.hexacore.tayo.category.CategoryRepository;
import com.hexacore.tayo.category.SubcategoryRepository;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.ReservationRepository;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.model.User;
import com.hexacore.tayo.util.S3Manager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final CarDateRangeRepository carDateRangeRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ReservationRepository reservationRepository;
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
        if (!isIndexSizeEqualsToImageSize(createCarRequestDto.getImageIndexes(), createCarRequestDto.getImageFiles())) {
            // index 리스트 길이와 image 리스트 길이가 같지 않은 경우
            throw new GeneralException(ErrorCode.IMAGE_INDEX_MISMATCH);
        }

        // 등록에 필요한 정보 가져오기
        Subcategory subcategory = subcategoryRepository.findByName(createCarRequestDto.getCarName())
                // 존재하지 않는 모델인 경우 새로운 row 추가
                .orElseGet(() -> createSubcategory(createCarRequestDto.getCarName()));

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

    /* 서브 카테고리 추가 */
    @Transactional
    public Subcategory createSubcategory(String subcategoryName) {
        List<Category> categories = categoryRepository.findAll();

        Category category = categories.stream()
                .filter(c -> subcategoryName.contains(c.getName()))
                .findFirst()
                // 해당하는 카테고리가 없는 경우 ETC 카테고리로 등록
                .orElseGet(() -> categories.stream()
                        .filter(c -> "ETC".equals(c.getName()))
                        .findFirst().orElseThrow(() -> new GeneralException(ErrorCode.ETC_MODEL_NOT_FOUND)));

        return subcategoryRepository.save(Subcategory.builder().name(subcategoryName).category(category).build());
    }

    public Slice<SearchCarsResultDto> searchCars(SearchCarsDto searchCarsDto, Pageable pageable) {
        return carRepository.search(searchCarsDto, pageable);
    }

    /* 차량 정보 조회 */
    public GetCarResponseDto carDetail(Long carId) {
        Car car = carRepository.findByIdAndIsDeletedFalse(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));
        return new GetCarResponseDto(car, getCarAvailableDatesForGuest(car));
    }

    /* 차량 정보 수정 */
    @Transactional
    public void updateCar(Long carId, UpdateCarRequestDto updateCarRequestDto, Long userId) {
        if (!isIndexSizeEqualsToImageSize(updateCarRequestDto.getImageIndexes(), updateCarRequestDto.getImageFiles())) {
            // index 리스트 길이와 image 리스트 길이가 같지 않은 경우
            throw new GeneralException(ErrorCode.IMAGE_INDEX_MISMATCH);
        }
        Car car = carRepository.findByIdAndIsDeletedFalse(carId)
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
    public void deleteCar(Long carId, Long userId) {
        Car car = carRepository.findByIdAndIsDeletedFalse(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        // 차량 주인이 아닌 경우 삭제불가
        if (!userId.equals(car.getOwner().getId())) {
            throw new GeneralException(ErrorCode.CAR_UPDATED_BY_OTHERS);
        }

        // 차량에 연결된 READY, USING 상태의 예약이 있는 경우 삭제불가
        if (isCarHavingReservation(car.getReservations())) {
            throw new GeneralException(ErrorCode.CAR_HAVE_ACTIVE_RESERVATIONS);
        }

        // 차량 삭제: isDeleted = true
        car.setIsDeleted(true);
        carRepository.save(car);

        // CarDateRange 삭제
        carDateRangeRepository.deleteAll(car.getCarDateRanges());

        // 이미지 삭제
        carImageRepository.findByCar_Id(car.getId()).forEach((image) -> {
            // s3 버킷 객체 삭제
            s3Manager.deleteImage(image.getUrl());
            carImageRepository.delete(image);
        });
    }

    /* 예약 가능 날짜 수정 */
    @Transactional
    public void updateDateRanges(Long hostUserId, Long carId,
            CarDateRangesDto carDateRangesDto) {
        // 차량 조회가 안 되는 경우
        Car car = carRepository.findByIdAndIsDeletedFalse(carId)
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        // 차량 소유자와 일치하지 않을 경우
        if (!car.getOwner().getId().equals(hostUserId)) {
            throw new GeneralException(ErrorCode.CAR_DATE_RANGE_UPDATED_BY_OTHERS);
        }

        // CarDateRanges 검증하고 인접한 구역을 합친다.
        // 검증 요소: List<LocalDate>의 길이는 2이고, 시작일자 <= 종료일자, 그리고 겹치는 부분이 없어야 한다.
        List<CarDateRangeDto> sortedCarDateRanges = checkDateRangesDtoValidAndMerge(carDateRangesDto);

        List<Reservation> reservations = reservationRepository.findAllByCar_idAndStatusInOrderByRentDateTimeAsc(
                car.getId(),
                List.of(ReservationStatus.USING, ReservationStatus.READY)
        );

        int reservationIdx = 0, dateRangeIdx = 0;
        while (dateRangeIdx < sortedCarDateRanges.size() && reservationIdx < reservations.size()) {
            CarDateRangeDto dateRange = sortedCarDateRanges.get(dateRangeIdx);
            Reservation reservation = reservations.get(reservationIdx);
            LocalDate dateRangeStartDate = dateRange.getStartDate();
            LocalDate dateRangeEndDate = dateRange.getEndDate();
            LocalDate reservationStartDate = reservation.getRentDateTime().toLocalDate();
            LocalDate reservationEndDate = reservation.getReturnDateTime().toLocalDate();

            // dateRangeStartDate <= reservationStartDate <= reservationEndDate <= dateRangeEndDate
            if (!dateRangeStartDate.isAfter(reservationStartDate) // 예약 가능한 구간에 예약이 포함하면 다음 예약 확인
                    && !reservationEndDate.isAfter(dateRangeEndDate)) {
                reservationIdx++;
            } else if (reservationStartDate.isAfter(dateRangeEndDate)) { // 다음 예약 가능한 구간 확인
                dateRangeIdx++;
            } else {
                throw new GeneralException(ErrorCode.CAR_DATE_RANGE_NOT_CONTAIN_RESERVATIONS);
            }
        }

        if (reservationIdx < reservations.size()) { // 예약 가능한 구간이 남지 않았는데 예약이 남은 경우
            throw new GeneralException(ErrorCode.CAR_DATE_RANGE_NOT_CONTAIN_RESERVATIONS);
        }

        // 기존 구간을 모두 삭제한다.
        carDateRangeRepository.deleteAllByCar_Id(car.getId());

        sortedCarDateRanges.stream()
                .map(carDateRangeDto -> carDateRangeDto.toEntity(car))
                .forEach(carDateRangeRepository::save);
    }

    /* 이미지 엔티티 저장 */
    @Transactional
    public void saveImages(List<Integer> indexes, List<MultipartFile> files, Car car) {
        if (indexes == null || files == null) {
            return;
        }

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

            Optional<CarImage> optionalImage = carImageRepository.findByCar_IdAndOrderIdx(
                    car.getId(), idx);
            CarImage carImage;

            if (optionalImage.isPresent()) {
                carImage = optionalImage.get();
                s3Manager.deleteImage(carImage.getUrl());
                carImageRepository.delete(carImage);
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
        return carRepository.findByOwner_IdAndIsDeletedFalse(userId).isPresent();
    }

    /* 중복된 차량 번호가 있는지 체크 */
    private Boolean isCarNumberDuplicated(String carNumber) {
        return !carRepository.findByCarNumberAndIsDeletedFalse(carNumber).isEmpty();
    }

    /* 인덱스 리스트와 이미지 리스트의 사이즈가 같은지 체크 */
    private Boolean isIndexSizeEqualsToImageSize(List<Integer> imageIndexes, List<MultipartFile> imageFiles) {
        if (imageIndexes == null && imageFiles == null) {
            return true;
        } else if (imageIndexes == null || imageFiles == null) {
            return false;
        }
        return imageIndexes.size() == imageFiles.size();
    }

    /* CarDateRangesDto가 올바른지 검증하고 정렬한뒤 인접하다면 병합한다. */
    private List<CarDateRangeDto> checkDateRangesDtoValidAndMerge(CarDateRangesDto carDateRangesDto) {
        List<List<LocalDate>> dateRanges = carDateRangesDto.getDates();
        // dateRanges가 비어있다면 모든 예약일자를 비운다는 의미이다.
        if (dateRanges.isEmpty()) {
            return List.of();
        }

        // 각 구간이 [시작, 끝] 으로 이루어지지 않거나 시작 날짜가 끝 날짜보다 뒤에 있는 경우
        for (List<LocalDate> dateRange : dateRanges) {
            if (dateRange.size() != 2) {
                throw new GeneralException(ErrorCode.DATE_SIZE_MISMATCH);
            }

            LocalDate startDate = dateRange.get(0);
            LocalDate endDate = dateRange.get(1);

            if (startDate.isAfter(endDate)) {
                throw new GeneralException(ErrorCode.DATE_FORMAT_MISMATCH);
            }
        }

        Function<List<LocalDate>, LocalDate> firstSort = dateRange -> dateRange.get(0);
        Function<List<LocalDate>, LocalDate> secondSort = dateRange -> dateRange.get(1);

        dateRanges.sort(Comparator.comparing(firstSort)
                .thenComparing(secondSort));

        List<CarDateRangeDto> result = new ArrayList<>();
        List<LocalDate> currentCarDateRange = dateRanges.get(0);

        for (int idx = 1; idx < dateRanges.size(); idx++) {
            List<LocalDate> nextCarDateRange = dateRanges.get(idx);
            LocalDate currentEndDate = currentCarDateRange.get(1);
            LocalDate nextStartDate = nextCarDateRange.get(0);
            LocalDate nextEndDate = nextCarDateRange.get(1);
            if (!currentEndDate.isBefore(nextStartDate)) {
                throw new GeneralException(ErrorCode.INVALID_CAR_DATE_RANGE_DUPLICATED);
            }

            if (currentEndDate.plusDays(1).isEqual(nextStartDate)) {
                currentCarDateRange.set(1, nextEndDate);
            } else {
                result.add(new CarDateRangeDto(currentCarDateRange));
                currentCarDateRange = nextCarDateRange;
            }
        }

        result.add(new CarDateRangeDto(currentCarDateRange));
        return result;
    }

    /* READY, USING 상태의 예약이 있는지 체크 */
    private Boolean isCarHavingReservation(List<Reservation> reservations) {
        return reservations.stream().anyMatch(reservation ->
                reservation.getStatus() == ReservationStatus.READY ||
                        reservation.getStatus() == ReservationStatus.USING);
    }

    private List<List<String>> getCarAvailableDatesForGuest(Car car) {
        List<List<String>> result = new ArrayList<>();
        List<CarDateRange> carAvailableDates = car.getCarDateRanges().stream()
                .filter(carDateRange -> carDateRange.getEndDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(CarDateRange::getStartDate))
                .toList();

        //가능일이 없으면 종료
        if (carAvailableDates.isEmpty()) {
            return result;
        }
        //status가 CANCEL이 아닌 예약 리스트를 시작날짜의 오름 차순으로 정렬
        List<Reservation> sortedReservations = car.getReservations().stream()
                .filter((reservation -> reservation.getStatus() != ReservationStatus.CANCEL))
                .filter(reservation -> !reservation.getReturnDateTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Reservation::getRentDateTime))
                .toList();

        int availableDatesIndex = 0;
        int reservationsIndex = 0;

        LocalDate start = carAvailableDates.get(0).getStartDate();
        LocalDate end;

        if (start.isBefore(LocalDate.now())) {
            start = LocalDate.now();
        }

        while (reservationsIndex < sortedReservations.size()) {
            Reservation reservation = sortedReservations.get(reservationsIndex);
            //예약이 예약 가능일의 범위에 포함되지 않으면 다음 예약 가능일로 이동
            if (carAvailableDates.get(availableDatesIndex).getEndDate()
                    .isBefore(reservation.getReturnDateTime().toLocalDate())) {
                end = carAvailableDates.get(availableDatesIndex).getEndDate();
                if (!start.isAfter(end)) {
                    result.add(List.of(start.toString(), end.toString()));
                }
                start = carAvailableDates.get(++availableDatesIndex).getStartDate();
                continue;
            }

            end = reservation.getRentDateTime().toLocalDate().minusDays(1);
            if (!start.isAfter(end)) {
                result.add(List.of(start.toString(), end.toString()));
            }
            start = reservation.getReturnDateTime().toLocalDate().plusDays(1);
            reservationsIndex++;
        }
        end = carAvailableDates.get(availableDatesIndex++).getEndDate();
        if (!start.isAfter(end)) {
            result.add(List.of(start.toString(), end.toString()));
        }

        for (int i = availableDatesIndex; i < carAvailableDates.size(); i++) {
            CarDateRange date = carAvailableDates.get(i);
            result.add(List.of(date.getStartDate().toString(), date.getEndDate().toString()));
        }

        return result;
    }
}
