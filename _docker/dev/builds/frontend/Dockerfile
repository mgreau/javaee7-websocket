# Pull base image.
FROM node:0.12.4

ENV APP_USER mgreau

# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN useradd --create-home --user-group --shell /bin/bash ${APP_USER}


# Install Bower & Grunt
RUN npm install -g bower grunt-cli

USER ${APP_USER}

# Define working directory.
WORKDIR /data

# Define default command.
CMD ["bash"]
