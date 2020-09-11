### Build
`docker-compose up --build`

### Run
`http://localhost:8080/request` and check the logs. You will see that the log message `Message Received: Message A` will show up 2 times although max-attempts is set to 1
