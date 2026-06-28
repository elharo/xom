package nu.xom;

import java.lang.management.*;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

class MemoryMonitor {
  
  static void attachMonitor(final XOMHandler handler, double thresholdPercentage) {
    
    for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
      if (pool.getType() == MemoryType.HEAP && pool.isCollectionUsageThresholdSupported()) {
        long maxMemory = pool.getUsage().getMax();
        long threshold = (long) (maxMemory * thresholdPercentage);
        pool.setCollectionUsageThreshold(threshold);

        NotificationEmitter emitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
        emitter.addNotificationListener(new NotificationListener() {
          @Override
          public void handleNotification(Notification notification, Object handback) {
            if (notification.getType().equals(MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED)) {
              handler.requestAbort();
            }
          }
        }, null, null);
      }
    }
    
  }

}