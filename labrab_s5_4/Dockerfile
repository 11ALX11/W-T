FROM node:lts
WORKDIR  /app
USER node
COPY . . 
COPY --chown=node:node ./app/package.json ./app/package-lock.json ./
RUN npm ci
COPY --chown=node:node . .
