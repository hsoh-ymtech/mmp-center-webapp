APP_NAME=mmp-center-web-site
VERSION=0.0.1

# DOCKER TASKS
export: ## Build the container
	rm -rf $(APP_NAME)-export.tar
	docker export ${APP_NAME} >  $(APP_NAME)-export.tar

import: ## import
	docker import $(APP_NAME)-export.tar ${APP_NAME}

save: ## save
	rm -rf $(APP_NAME).tar
	docker save -o $(APP_NAME).tar ${APP_NAME}

load: ## load
	docker load -i $(APP_NAME).tar

# Build the container
build: ## Build the container
	docker build -t $(APP_NAME) .

build-nc: ## Build the container without caching
	docker build --no-cache -t $(APP_NAME) .

#run: ## Run container on port configured in `config.env`
#       docker run -i -t --rm --env-file=./config.env -p=$(PORT):$(PORT) --name="$(APP_NAME)" $(APP_NAME)
#       docker run --privileged -it --rm -v /sys/fs/cgroup:/sys/fs/cgroup --name="$(APP_NAME)" $(APP_NAME) /usr/sbin/init

run: ## Run container on port configured in `config.env`
	#docker run -d --name "${APP_NAME}" --privileged=true -it -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:ro ${APP_NAME} /usr/sbin/init
	docker run --rm -d --name "${APP_NAME}" --privileged=true -it -e container=docker -p 80:80 -v /sys/fs/cgroup:/sys/fs/cgroup:ro ${APP_NAME} 

up: build run ## Run container on port configured in `config.env` (Alias to run)

stop: ## Stop and remove a running container
	docker stop $(APP_NAME); docker rm $(APP_NAME)

release: build-nc publish ## Make a release by building and publishing the `{version}` ans `latest` tagged containers to ECR

# Docker publish
publish: repo-login publish-latest publish-version ## Publish the `{version}` ans `latest` tagged containers to ECR

publish-latest: tag-latest ## Publish the `latest` taged container to ECR
	@echo 'publish latest to $(DOCKER_REPO)'
	docker push $(DOCKER_REPO)/$(APP_NAME):latest

publish-version: tag-version ## Publish the `{version}` taged container to ECR
	@echo 'publish $(VERSION) to $(DOCKER_REPO)'
	docker push $(DOCKER_REPO)/$(APP_NAME):$(VERSION)

# Docker tagging
tag: tag-latest tag-version ## Generate container tags for the `{version}` ans `latest` tags

tag-latest: ## Generate container `{version}` tag
	@echo 'create tag latest'
	docker tag $(APP_NAME) $(DOCKER_REPO)/$(APP_NAME):latest

tag-version: ## Generate container `latest` tag
	@echo 'create tag $(VERSION)'
	docker tag $(APP_NAME) $(DOCKER_REPO)/$(APP_NAME):$(VERSION)

# HELPERS

# generate script to login to aws docker repo
CMD_REPOLOGIN := "eval $$\( aws ecr"
ifdef AWS_CLI_PROFILE
CMD_REPOLOGIN += " --profile $(AWS_CLI_PROFILE)"
endif
ifdef AWS_CLI_REGION
CMD_REPOLOGIN += " --region $(AWS_CLI_REGION)"
endif
CMD_REPOLOGIN += " get-login --no-include-email \)"

# login to AWS-ECR
repo-login: ## Auto login to AWS-ECR unsing aws-cli
	@eval $(CMD_REPOLOGIN)

version: ## Output the current version
	@echo $(VERSION)

