package com.simply.Cinema.service.support.impl;

import com.simply.Cinema.core.support.dto.SupportDto;
import com.simply.Cinema.core.support.entity.Support;
import com.simply.Cinema.core.support.repository.SupportRepo;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.service.support.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportServiceImpl implements SupportService {

    private final SupportRepo supportRepo;

    @Override
    public SupportDto createSupportTicket(SupportDto supportDto) throws BusinessException {
        log.info("Creating support ticket for user: {} with role: {}", supportDto.getUserId(), supportDto.getUserRole());
        Support support = Support.builder()
                .description(supportDto.getDescription())
                .status("OPEN")
                .isResolved(false)
                .userId(supportDto.getUserId())
                .userRole(supportDto.getUserRole())
                .build();

        Support saved = supportRepo.save(support);
        return mapToDto(saved);
    }

    @Override
    public SupportDto updateSupportStatus(Long ticketId, String status, boolean isResolved) throws BusinessException {
        log.info("Updating status for ticket ID: {} to {}", ticketId, status);
        Support support = supportRepo.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Support ticket not found"));

        support.setStatus(status);
        support.setResolved(isResolved);
        
        Support updated = supportRepo.save(support);
        return mapToDto(updated);
    }

    @Override
    public List<SupportDto> getAllTickets() {
        return supportRepo.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportDto> getTicketsByUserId(Long userId) {
        return supportRepo.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportDto> getTicketsByRole(String role) {
        return supportRepo.findByUserRole(role).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SupportDto getTicketById(Long id) throws BusinessException {
        Support support = supportRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Support ticket not found"));
        return mapToDto(support);
    }

    private SupportDto mapToDto(Support s) {
        return SupportDto.builder()
                .id(s.getId())
                .description(s.getDescription())
                .status(s.getStatus())
                .isResolved(s.isResolved())
                .userId(s.getUserId())
                .userRole(s.getUserRole())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
