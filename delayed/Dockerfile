FROM rabbitmq:3-management

COPY rabbitmq_delayed_message_exchange-20171201-3.7.x.ez /usr/lib/rabbitmq/lib/rabbitmq_server-3.7.4/plugins/rabbitmq_delayed_message_exchange-20171201-3.7.x.ez

RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange
