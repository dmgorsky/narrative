sbt docker:stage
docker build -t narrative-test -f docker/Dockerfile .
docker run -it -p 18083:8080 -p 5432:5432 --privileged --rm narrative-test
