package com.hexacore.tayo.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    Cookie accessTokenCookie;

    @BeforeEach
    public void setup() throws Exception {
        accessTokenCookie = new Cookie("accessToken", obtainAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
    }

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
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars")
                        .file(image1)
                        .cookie(accessTokenCookie)
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
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/{carId}", carId)
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("GET /cars 성공")
    void getCarTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .carDetail(Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/34")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Update /cars 성공")
    void updateCarSuccessTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .updateCar(Mockito.any(Long.class), Mockito.any(UpdateCarRequestDto.class), Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars/34")
                        .file(image1)
                        .cookie(accessTokenCookie)
                        .param("feePerHour", "10000")
                        .param("address", "경기도 테스트 주소")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5")
                        .with(request -> { // 요청 방식을 PUT으로 변경
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Update /cars feePerHour 없을때")
    void updateCarNoFeePerHourTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .updateCar(Mockito.any(Long.class), Mockito.any(UpdateCarRequestDto.class), Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars/34")
                        .file(image1)
                        .cookie(accessTokenCookie)
                        .param("address", "경기도 테스트 주소")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5")
                        .with(request -> { // 요청 방식을 PUT으로 변경
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Update /cars address 없을때")
    void updateCarNoAddressTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .updateCar(Mockito.any(Long.class), Mockito.any(UpdateCarRequestDto.class), Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars/34")
                        .file(image1)
                        .cookie(accessTokenCookie)
                        .param("feePerHour", "12000")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5")
                        .with(request -> { // 요청 방식을 PUT으로 변경
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Update /cars position 없을때")
    void updateCarPositionTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .updateCar(Mockito.any(Long.class), Mockito.any(UpdateCarRequestDto.class), Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars/34")
                        .file(image1)
                        .cookie(accessTokenCookie)
                        .param("feePerHour", "12000")
                        .param("address", "경기도 테스트 주소")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5")
                        .with(request -> { // 요청 방식을 PUT으로 변경
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Update /cars imageIndexes 없을때")
    void updateCarNoImageIndexesTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .updateCar(Mockito.any(Long.class), Mockito.any(UpdateCarRequestDto.class), Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars/34")
                        .file(image1)
                        .cookie(accessTokenCookie)
                        .param("feePerHour", "12000")
                        .param("address", "경기도 테스트 주소")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .with(request -> { // 요청 방식을 PUT으로 변경
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Update /cars imageFiles 없을때")
    void updateCarNoImageFilesTest() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile("imageFiles", "filename1.png", "image/png",
                "<<png data>>".getBytes());

        BDDMockito.doNothing().when(carService)
                .updateCar(Mockito.any(Long.class), Mockito.any(UpdateCarRequestDto.class), Mockito.any(Long.class));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cars/34")
                        .cookie(accessTokenCookie)
                        .param("feePerHour", "12000")
                        .param("address", "경기도 테스트 주소")
                        .param("position.lat", "37.5665")
                        .param("position.lng", "126.9780")
                        .param("description", "설명")
                        .param("imageIndexes", "1", "2", "3", "4", "5")
                        .with(request -> { // 요청 방식을 PUT으로 변경
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private String obtainAccessToken() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("tan@tan.com1", "12345");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String tokenString = result.getResponse().getHeader("Set-Cookie");
        int start = tokenString.indexOf("=") + 1; // "=" 다음 위치
        int end = tokenString.indexOf(";"); // ";" 위치
        String accessToken = tokenString.substring(start, end);
        return accessToken;
    }
}
