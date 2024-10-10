package ua.edu.ukma.cleaning.order;

import ua.edu.ukma.cleaning.jms.models.UserEvent;

public interface UserDeletingProcessor {
    void processUserDeleting(UserEvent userDeleteEvent);
}
