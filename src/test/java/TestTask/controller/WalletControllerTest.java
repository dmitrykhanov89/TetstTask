package TestTask.controller;

import TestTask.dto.WalletOperationRequest;
import TestTask.exception.InsufficientFundsException;
import TestTask.exception.WalletNotFoundException;
import TestTask.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private WalletOperationRequest deposit(UUID id) {
        return new WalletOperationRequest(id,
                WalletOperationRequest.OperationType.DEPOSIT,
                100);
    }

    private WalletOperationRequest withdraw(UUID id) {
        return new WalletOperationRequest(id,
                WalletOperationRequest.OperationType.WITHDRAW,
                50);
    }

    @Test
    void operate_WhenValidDeposit_ReturnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(deposit(UUID.randomUUID()))))
                .andExpect(status().isOk());
    }

    @Test
    void operate_WhenValidWithdraw_ReturnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(withdraw(UUID.randomUUID()))))
                .andExpect(status().isOk());
    }

    @Test
    void operate_WhenInsufficientFunds_ReturnsConflict() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new InsufficientFundsException("Insufficient funds"))
                .when(walletService)
                .processOperation(any());

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(withdraw(id))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void operate_WhenInvalidAmount_ReturnsBadRequest() throws Exception {
        WalletOperationRequest request =
                new WalletOperationRequest(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.DEPOSIT,
                        -10);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void operate_WhenInvalidJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getBalance_WhenWalletExists_ReturnsBalance() throws Exception {
        UUID id = UUID.randomUUID();

        given(walletService.getBalance(id)).willReturn(500L);

        mockMvc.perform(get("/api/v1/wallets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(id.toString()))
                .andExpect(jsonPath("$.balance").value(500));
    }

    @Test
    void getBalance_WhenWalletNotExists_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        given(walletService.getBalance(id)).willThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet not found"));
    }
}