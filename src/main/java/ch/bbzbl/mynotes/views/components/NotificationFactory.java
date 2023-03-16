package ch.bbzbl.mynotes.views.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;


/**
 * Factory Class for creating Notification Objects
 * @author Dani Herzka
 *
 */
public class NotificationFactory {
	
	private NotificationFactory() {}
	
	public static Notification successNotification(String message) {
		Notification successNotification = new Notification(message, 2000);
		successNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		return successNotification;
	}
	
	public static Notification errorNotification(String message) {
		Notification successNotification = new Notification(message, 4000);
		successNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		return successNotification;
	}

}
