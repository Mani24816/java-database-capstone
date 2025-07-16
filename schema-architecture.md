Section 1: 
Architecture Summary
This Spring Boot application follows a layered architecture incorporating both MVC and REST principles. It uses Thymeleaf templates for rendering dynamic HTML views for the Admin and Doctor dashboards, providing a server-side rendered UI. For other functionalities such as managing patients, appointments, and prescriptions, the application exposes RESTful APIs that return JSON responses.

The application connects to two databases to separate different types of data. MySQL is used for storing structured data such as information related to patients, doctors, appointments, and admins using JPA entities. MongoDB is used for handling unstructured or semi-structured data like prescriptions, utilizing document models. All incoming requests from controllers—whether REST or MVC—are routed through a common service layer, which contains the business logic. This layer communicates with repository interfaces that abstract the actual database interactions.

Section 2:
Numbered Flow of Data and Control
A user (Admin or Doctor) accesses the application via a web browser or an API client (like Postman or frontend JavaScript).

The request is routed to either a Thymeleaf MVC controller (for dashboards) or a REST controller (for APIs like patients, appointments, prescriptions).

The controller invokes the appropriate service method, which encapsulates the business logic.

The service layer determines which database (MySQL or MongoDB) is involved and calls the corresponding repository interface.

The JPA repository communicates with the MySQL database using SQL for structured data, while the Mongo repository communicates with the MongoDB database for unstructured data like prescriptions.

The retrieved data or result (success/failure) is returned back from the repository to the service, and then from the service to the controller.

Finally, the controller either:

Renders a Thymeleaf view (HTML page) for the user in the case of Admin or Doctor dashboard, or

Returns a JSON response for API consumers (like mobile apps or frontend applications).
