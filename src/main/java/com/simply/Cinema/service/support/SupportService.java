package com.simply.Cinema.service.support;

import com.simply.Cinema.core.support.dto.SupportDto;
import com.simply.Cinema.exception.BusinessException;

import java.util.List;

public interface SupportService {
    SupportDto createSupportTicket(SupportDto supportDto) throws BusinessException;
    SupportDto updateSupportStatus(Long ticketId, String status, boolean isResolved) throws BusinessException;
    List<SupportDto> getAllTickets();
    List<SupportDto> getTicketsByUserId(Long userId);
    List<SupportDto> getTicketsByRole(String role);
    SupportDto getTicketById(Long id) throws BusinessException;
}
