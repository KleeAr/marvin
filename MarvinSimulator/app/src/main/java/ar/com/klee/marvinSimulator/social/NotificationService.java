package ar.com.klee.marvinSimulator.social;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.STTService;
import ar.com.klee.marvinSimulator.voiceControl.handlers.LeerWhatsappHandler;

public class NotificationService extends AccessibilityService {

    public static NotificationService instance;
    public boolean wasListening = false;
    public String lastContact;

    private List<String> contacts = new ArrayList<>();
    private HashMap<String,Integer> numberOfMessages = new HashMap<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if(event.getPackageName().toString().equals("com.whatsapp")) {
                String s = event.getText().toString();
                s = s.replace("[Mensaje de ", "");
                lastContact = s.replace("]", "");

                int i = 0;

                if (STTService.getInstance().getIsListening()) {

                    wasListening = true;

                    while (i < contacts.size()) {
                        if (contacts.get(i).equals(s))
                            break;
                        i++;
                    }

                    if (i == contacts.size()) {
                        contacts.add(s);
                        numberOfMessages.put(s, 1);
                    } else
                        numberOfMessages.put(s, numberOfMessages.get(s) + 1);
                } else {

                    wasListening = false;

                    STTService.getInstance().setIsListening(true);
                    CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
                    ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsDisabled();
                    commandHandlerManager.setCurrentCommandHandler(new LeerWhatsappHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
                    commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "leer whatsapp")));
                }
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);

        instance = this;

    }

    @Override
    public void onInterrupt() {

    }

    public static boolean isInstanceInitialized(){
        if(instance == null)
            return false;
        return true;
    }

    public boolean checkMessageList(){
        if(contacts.size()!=0)
            return true;
        return false;
    }

    public String getNextContact(){
        return contacts.remove(0);
    }

    public int getNumberOfMessages(String contact){
        int msjs = numberOfMessages.get(contact);
        numberOfMessages.remove(contact);
        return msjs;
    }

}
