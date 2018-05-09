package com.jaimebrolesi.springamqpexample;

import com.jaimebrolesi.springamqpexample.producer.Producer;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
@RestController
public class SpringAmqpExampleApplication {

    private static final String QUEUE = "eventQueue";

    public static void main(String[] args) {
        final ConfigurableApplicationContext run = SpringApplication.run(SpringAmqpExampleApplication.class, args);
        final Producer bean = run.getBean(Producer.class);

        int count = 1;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while(count > 0) {
            final LocalDateTime now = LocalDateTime.now();
            bean.send(QUEUE, "processing message {0} with delay of {1}", now);
            count -= 1;
            System.out.println(count);
        }

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    @Autowired
    private JmsTemplate queueJmsTemplate;

    @Autowired
    private JmsTemplate topicJmsTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/delete/{messageId}")
    public void delete(@PathVariable String messageId) {
        topicJmsTemplate.convertAndSend(ScheduledMessage.AMQ_SCHEDULER_MANAGEMENT_DESTINATION, "delete", delayed -> {
            delayed.setStringProperty(ScheduledMessage.AMQ_SCHEDULER_ACTION, ScheduledMessage.AMQ_SCHEDULER_ACTION_REMOVE);
            delayed.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_ID, messageId);
            System.out.println("Deleting message: " + messageId);
            return delayed;
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "/delete/interval")
    public void deleteInterval() {
        topicJmsTemplate.convertAndSend(ScheduledMessage.AMQ_SCHEDULER_MANAGEMENT_DESTINATION, "delete", delayed -> {
            delayed.setStringProperty(ScheduledMessage.AMQ_SCHEDULER_ACTION, ScheduledMessage.AMQ_SCHEDULER_ACTION_REMOVEALL);
            final long start = LocalDateTime.now().minusHours(4).toInstant(ZoneOffset.UTC).toEpochMilli();
            final long end = LocalDateTime.now().minusHours(3).toInstant(ZoneOffset.UTC).toEpochMilli();
            delayed.setStringProperty(ScheduledMessage.AMQ_SCHEDULER_ACTION_START_TIME, Long.toString(start));
            delayed.setStringProperty(ScheduledMessage.AMQ_SCHEDULER_ACTION_END_TIME, Long.toString(end));
            return delayed;
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "/deleteAll")
    public void deleteAll() {
        topicJmsTemplate.convertAndSend(ScheduledMessage.AMQ_SCHEDULER_MANAGEMENT_DESTINATION, "delete", delayed -> {
            delayed.setStringProperty(ScheduledMessage.AMQ_SCHEDULER_ACTION, ScheduledMessage.AMQ_SCHEDULER_ACTION_REMOVEALL);
            return delayed;
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "/countAll")
    public int countAll() {
        return queueJmsTemplate.browse(QUEUE, (session, browser) -> {
            Enumeration enumeration = browser.getEnumeration();
            int counter = 0;
            while (enumeration.hasMoreElements()) {
                Message msg = (Message) enumeration.nextElement();
                counter += 1;
                System.out.println(String.format("\tFound : %s", counter));
            }
            return counter;
        });
    }
}
