package com.hexacore.tayo.car;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class carDetailTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    @DisplayName("carDetail service test")
    void CarDetailServiceTest() throws Exception {
        // given

        // when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.carNumber").value("60주 6000"));
    }
}
