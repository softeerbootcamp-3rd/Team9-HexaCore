package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.ModelEntity;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.model.UserEntity;
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
    private ModelRepository modelRepository;

    private UserEntity user;
    private ModelEntity model;

    @BeforeEach
    void createUserAndModel() {
        UserEntity user = userRepository.save(
                UserEntity.builder().email("test@test.com").name("테스트").nickname("테스트 유저").phoneNumber("010-0000-0000")
                        .password("1234")
                        .build());
        ModelEntity model = modelRepository.save(
                ModelEntity.builder().category("모델명").subCategory("모델명 세부모델명").build());
        this.user = user;
        this.model = model;
    }

    @Test
    @DisplayName("ownerId와 carNumber로 검색해서 isDelete = true인 CarEntity를 반환한다")
    void findByOwner_IdAndCarNumberAndIsDeletedTrue() {
        // given
        String carNumber = "11주 1111";
        carRepository.save(
                CarEntity.builder().owner(user).model(model).carNumber(carNumber).type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position(new GeometryFactory().createPoint(new Coordinate(10.0, 10.0)))
                        .isDeleted(true).build());

        // when
        Optional<CarEntity> car = carRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(user.getId(), carNumber);

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
                CarEntity.builder().owner(user).model(model).carNumber("11주 1111").type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position(new GeometryFactory().createPoint(new Coordinate(10.0, 10.0)))
                        .isDeleted(false).build());

        // when
        List<CarEntity> cars = carRepository.findByOwner_IdAndIsDeletedFalse(user.getId());

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
                CarEntity.builder().owner(user).model(model).carNumber(carNumber).type(CarType.LIGHT).capacity(2)
                        .address("경기도 테스트 주소")
                        .feePerHour(1000).position(new GeometryFactory().createPoint(new Coordinate(10.0, 10.0)))
                        .isDeleted(false).build());

        // when
        List<CarEntity> cars = carRepository.findByCarNumberAndIsDeletedFalse(carNumber);

        // then
        Assertions.assertThat(cars.size()).isEqualTo(1);
        Assertions.assertThat(cars.get(0).getCarNumber()).isEqualTo(carNumber);
        Assertions.assertThat(cars.get(0).getIsDeleted()).isFalse();
    }
}
