package com.hexacore.tayo.car.model;

import com.hexacore.tayo.common.BaseTime;
import com.hexacore.tayo.user.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import org.springframework.data.util.Pair;

@Entity
@Getter
@Setter
@Table(name = "Car")
public class CarEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

    @Column(name = "car_number", nullable = false)
    private String carNumber;

    @Column(name = "mileage")
    private Double mileage;

    @Column(name = "fuel")
    private String fuel;

    @Column(name = "type", nullable = false)
    private CarType type;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "year")
    private Integer year;

    @Column(name = "fee_per_hour", nullable = false)
    private Integer feePerHour;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "position", nullable = false)
    private Point position;

    @Column(name = "description")
    private String description;

    @Column(name = "dates", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Pair<Date, Date>> dates;

    public void setDates(List<List<String>> datesString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Pair<Date, Date>> dates = new ArrayList<>();

        for (List<String> datePair : datesString) {
            if (datePair.size() != 2) {
                // 유효한 날짜 쌍이 아닌 경우 무시
                continue;
            }

            try {
                // Pair로 만들어서 dates에 추가하기
                Date startDate = dateFormat.parse(datePair.get(0));
                Date endDate = dateFormat.parse(datePair.get(1));

                dates.add(Pair.of(startDate, endDate));
            } catch (ParseException e) {
                // 날짜 문자열 파싱 오류 처리
                throw new RuntimeException(e);
            }
        }

        this.dates = dates;
    }
}
