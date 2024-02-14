package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    @DisplayName("POST /cars 컨트롤러 테스트")
    void createCarTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService).createCar(Mockito.any(CreateCarRequestDto.class), Mockito.anyLong());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cars")
                        .file(image1)
                        .param("carNumber", "11주 1111")
                        .param("carName", "모델명 서브모델명")
                        .param("mileage", "10.0")
                        .param("fuel", "가솔린")
                        .param("type", "경차")
                        .param("capacity", "2")
                        .param("year", "2020")
                        .param("feePerHour", "10000")
                        .param("address", "경기도 테스트 주소")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5").contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("POST /cars 컨트롤러 테스트: 필수 인자가 모두 전달되지 않은 경우")
    void createCarFailTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService).createCar(Mockito.any(CreateCarRequestDto.class), Mockito.anyLong());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cars")
                        .file(image1)
                        .param("carNumber", "11주 1111")
//                        .param("carName", "모델명 서브모델명")
                        .param("mileage", "10.0")
                        .param("fuel", "가솔린")
                        .param("type", "경차")
                        .param("capacity", "2")
                        .param("year", "2020")
                        .param("feePerHour", "10000")
                        .param("address", "경기도 테스트 주소")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5").contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /cars/:carId 컨트롤러 테스트")
    void deleteCarTest() throws Exception {
        // given
        Long carId = 1L;
        BDDMockito.doNothing().when(carService).deleteCar(Mockito.anyLong());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
