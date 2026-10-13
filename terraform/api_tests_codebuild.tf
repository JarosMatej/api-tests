data "aws_iam_role" "codebuild_role" {
  name = "codebuild-signosoft-performance-tests-service-role"
}

variable "codebuild_name" {
  description = "The name of the codebuild"
  type = string
  default = "rest-assured-api-tests"
}

resource "aws_codebuild_project" "rest-assured-api-tests" {
  name           =  var.codebuild_name
  service_role   = data.aws_iam_role.codebuild_role.arn
  description    = "CodeBuild project for executing signosoft automated API tests"
  build_timeout  = 10
  queued_timeout = 480

  logs_config {
    cloudwatch_logs {
      status = "ENABLED"
      group_name = "/aws/codebuild/${var.codebuild_name}"
    }
  }

  artifacts {
    type = "NO_ARTIFACTS"
  }
  source {
    type            = "GITHUB"
    location        = "https://github.com/JarosMatej/api-tests"
    buildspec       = "buildspec.yml"
    git_clone_depth = 1
  }
  environment {
    compute_type = "BUILD_GENERAL1_SMALL"
    image        = "public.ecr.aws/docker/library/maven:3.9.16-amazoncorretto-21"
    type         = "LINUX_CONTAINER"

    environment_variable {
      name  = "SERVER"
      value = "/testing/api/server"
      type  = "PARAMETER_STORE"
    }
    environment_variable {
      name  = "DOC_OWNER"
      value = "/testing/api/doc-owner"
      type  = "PARAMETER_STORE"
    }
    environment_variable {
      name  = "LOGIN"
      value = "/testing/api/user-login"
      type  = "PARAMETER_STORE"
    }
    environment_variable {
      name  = "PASSWORD"
      value = "/testing/api/password"
      type  = "PARAMETER_STORE"
    }
    environment_variable {
      name  = "SIGNER_LOGIN"
      value = "/testing/api/signer-login"
      type  = "PARAMETER_STORE"
    }
    environment_variable {
      name  = "NAME"
      value = "/testing/api/name"
      type  = "PARAMETER_STORE"
    }
  }
}
