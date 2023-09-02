package ggc.core.notifications;

import ggc.core.notifications.Notification;

public interface Subscriber {
    void update(Notification n);
}
