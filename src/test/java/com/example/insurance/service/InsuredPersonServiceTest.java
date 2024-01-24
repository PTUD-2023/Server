package com.example.insurance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.insurance.common.MapEntityToDTO;
import com.example.insurance.entity.InsuredPerson;
import com.example.insurance.repository.InsuredPersonRepository;

public class InsuredPersonServiceTest {

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private InsuredPersonRepository insuredPersonRepository;

    @InjectMocks
    private InsuredPersonService insuredPersonService;

    @Test
    void testGetInsurePerson_WithValidId_ReturnInsuredPersonEntity() {
        //Arange
        InsuredPerson mockInsuredPerson = mock(InsuredPerson.class);
        mockInsuredPerson.setId(1L);

        when(insuredPersonRepository.findById(anyLong())).thenReturn(Optional.of(mockInsuredPerson));

        //Action
        InsuredPerson actualInsuredPerson = insuredPersonService.getInsuredPerson(1L);

        //Assert
        assertThat(actualInsuredPerson).isEqualTo(mockInsuredPerson);
        assertThat(actualInsuredPerson).isNotNull();

        //Verify
        verify(insuredPersonRepository, times(1)).findById(1L);
    }

    @Test
    void testGetInsurePerson_WithInvalidId_ReturnNull() {
        //Arrange
        InsuredPerson mockInsuredPerson = mock(InsuredPerson.class);
        mockInsuredPerson.setId(-1L);

        when(insuredPersonRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Action
        InsuredPerson actualInsuredPerson = insuredPersonService.getInsuredPerson(-1L);

        //Assert
        assertThat(actualInsuredPerson).isNull();

        //Verify
        verify(insuredPersonRepository, times(1)).findById(-1L);
    }
    
    @Test
    void testCreateInsuredPerson_ReturnInsuredPerson() {
        //Arrange
        InsuredPerson mockInsuredPerson = mock(InsuredPerson.class);
        when(insuredPersonRepository.save(any(InsuredPerson.class)))
            .thenReturn(mockInsuredPerson);
        
        //Action
        InsuredPerson savedInsuredPerson = insuredPersonService.createInsuredPerson(mockInsuredPerson);

        //Assert
        assertNotNull(savedInsuredPerson);
        assertThat(savedInsuredPerson).isEqualTo(mockInsuredPerson);

        //Verify
        verify(insuredPersonRepository, times(1)).save(any(InsuredPerson.class));
    }
}
