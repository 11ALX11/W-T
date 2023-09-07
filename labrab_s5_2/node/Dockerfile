FROM node:20-alpine

WORKDIR /node
COPY ["package*.json", "./"]
RUN npm install
COPY ["./", "./"]
