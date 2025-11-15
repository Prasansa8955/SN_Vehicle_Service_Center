
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

    // Get vehicles sorted by priority
    public List<QueueItem> getQueueInPriorityOrder() {
        queueList.sort(Comparator.comparing((QueueItem q) -> q.priority));
        return new ArrayList<>(queueList);
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
