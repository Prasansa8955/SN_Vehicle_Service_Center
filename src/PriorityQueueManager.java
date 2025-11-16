import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityQueueManager {

    public enum Priority {
        VIP,
        URGENT,
        NORMAL
    }

    public static class QueueItem {
        String vehicleId;
        String customerName;
        String vehicleNumber;
        String vehicleType;
        String serviceType;
        String status;
        Priority priority;

        public QueueItem(String vehicleId, String customerName, String vehicleNumber,
                         String vehicleType, String serviceType, String status, Priority priority) {
            this.vehicleId = vehicleId;
            this.customerName = customerName;
            this.vehicleNumber = vehicleNumber;
            this.vehicleType = vehicleType;
            this.serviceType = serviceType;
            this.status = status;
            this.priority = priority;
        }
    }

    private final List<QueueItem> queueList = new ArrayList<>();

    // Add new vehicle
    public void add(QueueItem item) {
        queueList.add(item);
    }

    // Remove vehicle by ID
    public void remove(String vehicleId) {
        queueList.removeIf(q -> q.vehicleId.equals(vehicleId));
    }

    // Get vehicles sorted **alphabetically by priority name**
    public List<QueueItem> getQueueAlphabetical() {
        queueList.sort(Comparator.comparing(q -> q.priority.name()));
        return new ArrayList<>(queueList);
    }

    // Get vehicles sorted **by business priority: VIP > URGENT > NORMAL**
    public List<QueueItem> getQueueByPriority() {
        queueList.sort((q1, q2) -> Integer.compare(getPriorityValue(q1.priority), getPriorityValue(q2.priority)));
        return new ArrayList<>(queueList);
    }

    private int getPriorityValue(Priority p) {
        switch (p) {
            case VIP: return 1;
            case URGENT: return 2;
            case NORMAL: return 3;
            default: return 4;
        }
    }

    // Convert service type to priority
    public static Priority getPriorityFromServiceType(String serviceType) {
        switch (serviceType.toLowerCase()) {
            case "vip":
                return Priority.VIP;
            case "urgent":
                return Priority.URGENT;
            default:
                return Priority.NORMAL;
        }
    }
}
