# it-support-ticket-system
A Spring boot backend API of an IT-support ticket system which is being used
by the IT-support agents. The main object of the system is the support ticket which contains
information about the case. Each support ticket can have multiple comments added by the support
agent. The tickets have a lifecycle that contains the following states:
• In queue: new tickets that were submitted by a customer but not picked up by a support
agent yet
• In progress: When a support agent has started working on a ticket, they move it to this state
• Resolved: When a ticket is resolved/done

## Rest-Apis/service offered by this service
- CreateTicket : to create ticket without a comment
- getAllTickets : Return All Tickets in the system
- GetTicketInformation :Return all the information about a ticket including it's comments 
- UpdateTicket : update the status of a ticket and (optionally) add comment to it.
- CreateComment : add comment to an existing ticket
- GetTicketByCustomerId : Return All Customer's tickets.

## Techstack
- Java 11
-Spring Boot 2.7.6
-h2 in-memory Database
-Swagger
-Jib

## How to build docker Image localy from source
`./mvnw package  jib:dockerBuild`

## How to Run It
`docker run --publish=8080:8080 it-support-ticket-system`

## How To Test It using Swagger-ui
- open the following url in Chrome Broswer
  `http://localhost:8080/swagger-ui/index.html`

