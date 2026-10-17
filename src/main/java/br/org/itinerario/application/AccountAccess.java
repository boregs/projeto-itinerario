package br.org.itinerario.application;

import br.org.itinerario.domain.User;
import br.org.itinerario.infrastructure.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class AccountAccess {

    private final UserRepository users;

    public AccountAccess(UserRepository users) {
        this.users = users;
    }

    public User active(String email) {
        var user = users.findByEmail(email).orElseThrow(BusinessException::forbidden);
        if (user.status != User.Status.ACTIVE) throw BusinessException.forbidden();
        return user;
    }

    public User role(String email, User.Role role) {
        var user = active(email);
        if (user.role != role) throw BusinessException.forbidden();
        return user;
    }

    public User approvedMentor(String email) {
        var user = role(email, User.Role.MENTOR);
        if (
            user.mentorProfile == null ||
            user.mentorProfile.approvalStatus != User.Approval.APPROVED
        ) throw BusinessException.forbidden();
        return user;
    }

    public User lockRole(String email, User.Role role) {
        var user = users.lockByEmail(email).orElseThrow(BusinessException::forbidden);
        if (
            user.status != User.Status.ACTIVE || user.role != role
        ) throw BusinessException.forbidden();
        return user;
    }

    public User lockApprovedMentor(String email) {
        var user = lockRole(email, User.Role.MENTOR);
        if (
            user.mentorProfile == null ||
            user.mentorProfile.approvalStatus != User.Approval.APPROVED
        ) throw BusinessException.forbidden();
        return user;
    }
}
