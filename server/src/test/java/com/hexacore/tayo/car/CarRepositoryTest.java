package com.hexacore.tayo.car;

import com.hexacore.tayo.TestConfig;
import com.hexacore.tayo.car.carRepository.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.category.CategoryRepository;
import com.hexacore.tayo.category.SubcategoryRepository;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.Position;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.model.User;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubcategoryRepository subcategoryRepository;

    private User user;
    private Subcategory subcategory;

    @BeforeEach
    void createUserAndModel() {
        User user = userRepository.save(
                User.builder().email("test_code@test.com").name("테스트").phoneNumber("010-0000-0000")
                        .password("1234")
                        .build());
        Category category = categoryRepository.save(
                Category.builder().name("모델명").build());
        Subcategory subcategory = subcategoryRepository.save(
                Subcategory.builder().name("모델명 세부모델명").category(category).build());
        this.user = user;
        this.subcategory = subcategory;
    }

    @Test
    @DisplayName("ownerId와 carNumber로 검색해서 isDelete = true인 CarEntity를 반환한다")
    void findByOwner_IdAndCarNumberAndIsDeletedTrue() {
        // given
        String carNumber = "00주 0000";
        carRepository.save(
                Car.builder().owner(user).subcategory(subcategory).carNumber(carNumber).type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position((new Position(10.0, 10.0)).toPoint())
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
                Car.builder().owner(user).subcategory(subcategory).carNumber("11주 1111").type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position((new Position(10.0, 10.0)).toPoint())
                        .isDeleted(false).build());

        // when
       Optional<Car> car = carRepository.findByOwner_IdAndIsDeletedFalse(user.getId());

        // then
        Assertions.assertThat(car).isPresent();
        Assertions.assertThat(car.get().getOwner().getId()).isEqualTo(user.getId());
        Assertions.assertThat(car.get().getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("carNumber로 검색해서 isDeleted = false인 CarEntity 리스트를 반환한다")
    void findByCarNumberAndIsDeletedFalse() {
        // given
        String carNumber = "00주 0000";
        carRepository.save(
                Car.builder().owner(user).subcategory(subcategory).carNumber(carNumber).type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position((new Position(10.0, 10.0)).toPoint())
                        .isDeleted(false).build());

        // when
        List<Car> cars = carRepository.findByCarNumberAndIsDeletedFalse(carNumber);

        // then
        Assertions.assertThat(cars.size()).isEqualTo(1);
        Assertions.assertThat(cars.get(0).getCarNumber()).isEqualTo(carNumber);
        Assertions.assertThat(cars.get(0).getIsDeleted()).isFalse();
    }
}
