# Cloud Event Broadcaster API

The **Cloud Event Broadcaster API** is a lightweight, extensible Java library that allows you to broadcast cloud events
to various deliverers based on configurable channels. It provides a functional, fluent DSL to easily configure filters
and delivery listeners.

## Features

- **Fluent DSL:** Build your broadcaster configuration in a declarative way using the `spec()` method.
- **Extensibility:** Easily add custom filters and on-delivery actions.
- **Testability:** The design encourages separation of concerns, making it easier to unit test individual components.
- **Interfaces for Integration:** Define your own implementations for event delivery using provided interfaces.

## Installation

Include the library in your project as a dependency. For example add:

```groovy
dependencies {
    implementation 'com.github.j4c62:cloud-event-broadcaster-api:0.1.0-SNAPSHOT'
}
```

## Usage

The library uses a functional fluent pattern to configure and broadcast events. Below are examples to illustrate
different use cases.

### Basic Broadcasting

Broadcast all notifications without filtering:

```java
Broadcaster broadcaster = Broadcaster
    .from(delivererSelector, diffusibleComposer);

broadcaster.broadcast(cloudEvent);

```

### Using the Fluent DSL with Filters and Listeners

You can configure the broadcaster using the `spec()` method:

```java
Broadcaster broadcaster = Broadcaster
    .spec(delivererSelector, diffusibleComposer)
    .filter(notification -> notification.getChannel() != Channel.EMAIL)
    .onDelivery(event -> System.out.println("Delivered: " + event))
    .build();

broadcaster.broadcast(cloudEvent);
```

### Custom Use Cases

- **Audit Delivery:** Log each delivery event for auditing.
- **Selective Broadcasting:** Use custom predicates to filter notifications by type or channel.
- **Testing:** Inject mocks for `DelivererSelector` and `DiffusibleComposer` to simulate different scenarios.

## API Overview

### Core Interfaces

- **Diffusible:** Represents a cloud event with a defined channel.
- **Deliverer:** Extends `Diffusible` and provides a method to deliver the event.
- **DelivererSelector:** Interface to determine which deliverers should receive a given event.
- **DiffusibleComposer:** Interface to compose a list of notifications from a given event.
- **TriggerBroadcast:** Defines a trigger mechanism for broadcasting events.

### Main Classes

- **Broadcaster:** The central class responsible for broadcasting events. It groups deliverers by channel and dispatches
  notifications.
- **BroadcasterSpec:** A DSL interface to fluently build a configured `Broadcaster`.
- **DefaultBroadcasterSpec:** The default implementation of `BroadcasterSpec`.
- **BroadcastEvent:** A simple POJO encapsulating delivery events.

## Contributing

Feel free to fork the repository and submit pull requests. Any improvements or bug fixes are welcome!

## License

Distributed under the MIT License. See `LICENSE` for more information.
