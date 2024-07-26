# GenAI Springboot Application

This is a sample Spring Boot application that demonstrates the capabilities of Generative AI using the Amazon Bedrock API. The application allows you to interact with various Amazon Bedrock foundation models through different playgrounds, including text, chat, and image playgrounds.

## Features

- Text Playground: Interact with Amazon Bedrock foundation models using text input and output.
- Chat Playground: Engage in conversational interactions with the foundation models.
- Image Playground: Generate and manipulate images using the foundation models' capabilities.
- Foundation Model List: View the available Amazon Bedrock foundation models and their characteristics.

## Prerequisites

- Java Development Kit (JDK) installed
- AWS account with appropriate permissions to access Amazon Bedrock
- AWS credentials configured (e.g., AWS CLI, environment variables, or credentials file)

## Getting Started

1. Clone the repository:
   git clone https://github.com/your-repo/genai-springboot.git
2. Navigate to the project directory:
   cd genai-springboot
3. Build the project:
   ./mvnw clean package

4. Run the application:
   java -jar target/genai-springboot-0.0.1-SNAPSHOT.jar

5. Access the application in your web browser at `http://localhost:8080`.

## Configuration

The application uses the AWS SDK for Java to interact with Amazon Bedrock. You can configure the AWS credentials and region using environment variables or a credentials file.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## Acknowledgements

This project was inspired by and utilizes the following resources:

- [Spring Guide: Uploading Files](https://spring.io/guides/gs/uploading-files)
- [AWS Doc SDK Examples: Bedrock Runtime](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/bedrock-runtime)

## License

This project is licensed under the [MIT License](LICENSE).
