package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.Category;
import com.hexacore.tayo.car.model.SubCategory;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.model.User;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    private User user;
    private Category category;
    private SubCategory subCategory;

    @BeforeEach
    void createUserAndModel() {
        User user = userRepository.save(
                User.builder().email("test@test.com").name("테스트").nickname("테스트 유저").phoneNumber("010-0000-0000")
                        .password("1234")
                        .build());
        Category category = categoryRepository.save(
                Category.builder().name("모델명").build());
        SubCategory subCategory = subCategoryRepository.save(
                SubCategory.builder().name("세부모델명").build());
        this.user = user;
        this.category = category;
        this.subCategory = subCategory;
    }

    @Test
    @DisplayName("ownerId와 carNumber로 검색해서 isDelete = true인 CarEntity를 반환한다")
    void findByOwner_IdAndCarNumberAndIsDeletedTrue() {
        // given
        String carNumber = "11주 1111";
        carRepository.save(
                Car.builder().owner(user).subCategory(subCategory).carNumber(carNumber).type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position(new GeometryFactory().createPoint(new Coordinate(10.0, 10.0)))
                        .isDeleted(true).build());

        // when
        Optional<Car> car = carRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(user.getId(), carNumber);

        // then
        Assertions.assertThat(car).isPresent();
        Assertions.assertThat(car.get().getOwner().getId()).isEqualTo(user.getId());
        Assertions.assertThat(car.get().getCarNumber()).isEqualTo(carNumber);
        Assertions.assertThat(car.get().getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("ownerId로 검색해서 isDelete = false인 CarEntity 리스트를 반환한다")
    void findByOwner_IdAndIsDeletedFalse() {
        // given
        carRepository.save(
                Car.builder().owner(user).subCategory(subCategory).carNumber("11주 1111").type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position(new GeometryFactory().createPoint(new Coordinate(10.0, 10.0)))
                        .isDeleted(false).build());

        // when
        List<Car> cars = carRepository.findByOwner_IdAndIsDeletedFalse(user.getId());

        // then
        Assertions.assertThat(cars.size()).isEqualTo(1);
        Assertions.assertThat(cars.get(0).getOwner().getId()).isEqualTo(user.getId());
        Assertions.assertThat(cars.get(0).getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("carNumber로 검색해서 isDeleted = false인 CarEntity 리스트를 반환한다")
    void findByCarNumberAndIsDeletedFalse() {
        // given
        String carNumber = "11주 1111";
        carRepository.save(
                Car.builder().owner(user).subCategory(subCategory).carNumber(carNumber).type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position(new GeometryFactory().createPoint(new Coordinate(10.0, 10.0)))
                        .isDeleted(false).build());

        // when
        List<Car> cars = carRepository.findByCarNumberAndIsDeletedFalse(carNumber);

        // then
        Assertions.assertThat(cars.size()).isEqualTo(1);
        Assertions.assertThat(cars.get(0).getCarNumber()).isEqualTo(carNumber);
        Assertions.assertThat(cars.get(0).getIsDeleted()).isFalse();
    }
}
