package com.server.realsync.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.server.realsync.entity.Appointment;
import com.server.realsync.entity.Customer;
import com.server.realsync.repo.AppointmentRepository;
import com.server.realsync.repo.CustomerRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            CustomerRepository customerRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
    }

    public Appointment create(Appointment appointment, Integer customerId, Integer accountId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        appointment.setCustomer(customer);
        appointment.setAccountId(accountId);

        if (appointment.getStatus() == null) {
            appointment.setStatus("UPCOMING");
        }

        appointment.setCreatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAll(Integer accountId) {
        return appointmentRepository.findByAccountId(accountId);
    }

    public Optional<Appointment> getById(Long id, Integer accountId) {
        return appointmentRepository.findByIdAndAccountId(id, accountId);
    }

    public Appointment update(Appointment updated, Long id, Integer accountId) {

        Appointment existing = getById(id, accountId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        existing.setServiceName(updated.getServiceName());
        existing.setServiceType(updated.getServiceType());
        existing.setAssignee(updated.getAssignee());

        existing.setAppointmentDate(updated.getAppointmentDate());
        existing.setAppointmentTime(updated.getAppointmentTime());

        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setReminderType(updated.getReminderType());
        existing.setChannel(updated.getChannel());
        existing.setNotes(updated.getNotes());

        existing.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(existing);
    }

    public void delete(Long id, Integer accountId) {

        Appointment appt = getById(id, accountId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointmentRepository.delete(appt);
    }

    public Appointment updateStatus(Long id, Integer accountId, String status) {

        Appointment appt = getById(id, accountId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(status);
        appt.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appt);
    }

    public List<Appointment> getToday(Integer accountId) {
        return appointmentRepository.findByAccountIdAndAppointmentDate(
                accountId, LocalDate.now());
    }

    public List<Appointment> getUpcoming(Integer accountId) {
        return appointmentRepository
                .findByAccountIdAndAppointmentDateGreaterThanEqual(
                        accountId, LocalDate.now());
    }
}