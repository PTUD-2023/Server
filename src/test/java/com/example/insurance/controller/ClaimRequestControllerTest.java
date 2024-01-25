package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.component.Utils;
import com.example.insurance.dto.NewClaimRequest;
import com.example.insurance.entity.ClaimRequest;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClaimRequestControllerTest {
    @Mock
    private ClaimRequestService claimRequestService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private S3Service s3Service;
    @Mock
    private DocumentService documentService;
    @Mock
    private Utils utils;
    @InjectMocks
    private ClaimRequestController claimRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createClaimRequest_WithValidInput_ShouldReturnCreatedResponse() throws IOException {
        // Arrange
        String token = "Bearer token";
        NewClaimRequest newClaimRequest = mock(NewClaimRequest.class);
        List<MultipartFile> files = Collections.singletonList(mock(MultipartFile.class));

        // Mocking jwtService
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");

        // Mocking userAccountService
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1L);
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(userAccount));

        // Mocking claimRequestService
        ClaimRequest claimRequest = new ClaimRequest();
        claimRequest.setId(1L);
        when(claimRequestService.createClaimRequest(any())).thenReturn(claimRequest);

        // Mocking s3Service
        when(s3Service.uploadFileToS3(anyString(), any())).thenReturn("mocked-s3-url");

        // Mocking documentService
        doNothing().when(documentService).createDocument(any());

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.createClaimRequest(token, newClaimRequest, files);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Create new claim request", "Created");

        // Verify interactions
        verify(documentService, times(1)).createDocument(any());
    }

    @Test
    void createClaimRequest_WhenUserDoesNotExist_ShouldReturnNotFoundResponse() throws IOException {
        // Arrange
        String token = "Bearer token";
        NewClaimRequest newClaimRequest = mock(NewClaimRequest.class);
        List<MultipartFile> files = Collections.singletonList(mock(MultipartFile.class));

        // Mocking jwtService
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");

        // Mocking userAccountService
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1L);
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.createClaimRequest(token, newClaimRequest, files);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"CannotCreate","Could not create new claim request");
    }

    @Test
    void getAllClaimRequest_ShouldReturnClaimRequests() {
        // Arrange
        List<ClaimRequest> claimRequests = Collections.singletonList(mock(ClaimRequest.class));
        Page<ClaimRequest> expectedPage = new PageImpl<>(claimRequests);
        when(claimRequestService.getAllClaimRequest(any())).thenReturn(expectedPage);

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.getAllClaimRequest(0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedPage);
    }

    @Test
    void getClaimRequestByUserAccountId_ShouldReturnClaimRequests() {
        // Arrange
        String token = "Bearer some-token";
        String email = "johndoe@example.com";
        UserAccount userAccount = mock(UserAccount.class);
        when(userAccount.getId()).thenReturn(123L);
        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(userAccount));

        List<ClaimRequest> claimRequests = Collections.singletonList(mock(ClaimRequest.class));
        Page<ClaimRequest> expectedPage = new PageImpl<>(claimRequests);
        when(claimRequestService.getClaimRequestByUserAccountId(anyLong(), any(PageRequest.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.getClaimRequestByUserAccountId(token, 0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedPage);
    }

    @Test
    void getClaimRequestByUserAccountId_WithInvalidToken_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String token = "Invalid token";

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.getClaimRequestByUserAccountId(token, 0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void getClaimRequestByUserAccountId_WhenUserDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        String token = "Bearer some-token";
        String email = "johndoe@example.com";
        UserAccount userAccount = mock(UserAccount.class);
        when(userAccount.getId()).thenReturn(123L);
        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.getClaimRequestByUserAccountId(token, 0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");

    }

    @Test
    void approveClaimRequest_WithValidInput_ShouldReturnSuccessResponse() {
        // Arrange
        Long id = 123L;
        ClaimRequest claimRequest = mock(ClaimRequest.class);
        when(claimRequestService.updateStatusClaimRequest(id, "approved")).thenReturn(claimRequest);

        UserAccount userAccount = mock(UserAccount.class);
        when(userAccount.getEmail()).thenReturn("johndoe@example.com");
        when(userAccount.getLastName()).thenReturn("Doe");
        when(userAccountService.getUserById(id)).thenReturn(Optional.of(userAccount));
        doNothing().when(utils).sendResponseEmail(anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.approveClaimRequest(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Approved claim request successfully", "Approved");
    }

    @Test
    void approveClaimRequest_WhenClaimRequestDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        Long id = 123L;
        when(claimRequestService.updateStatusClaimRequest(id, "approved")).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.approveClaimRequest(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"CannotApproved","Cannot approve claim request");

    }

    @Test
    void refuseClaimRequest_WithValidInput_ShouldReturnSuccessResponse() {
        // Arrange
        Long id = 123L;
        ClaimRequest claimRequest = mock(ClaimRequest.class);
        when(claimRequestService.updateStatusClaimRequest(id, "refused")).thenReturn(claimRequest);

        UserAccount userAccount = mock(UserAccount.class);
        when(userAccount.getEmail()).thenReturn("johndoe@example.com");
        when(userAccount.getLastName()).thenReturn("Doe");
        when(userAccountService.getUserById(id)).thenReturn(Optional.of(userAccount));
        doNothing().when(utils).sendResponseEmail(anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.refuseClaimRequest(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Refused claim request successfully", "Refused");
    }

    @Test
    void refuseClaimRequest_WhenClaimRequestDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        Long id = 123L;
        when(claimRequestService.updateStatusClaimRequest(id, "refused")).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = claimRequestController.refuseClaimRequest(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"CannotRefuse","Cannot refuse claim request");

    }
}