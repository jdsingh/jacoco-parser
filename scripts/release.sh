#!/usr/bin/env bash

APP_VERSION="0.0.1"

print_info() {
  # shellcheck disable=SC2059
  printf "$1\n"
}

print_warning() {
  YELLOW='\033[0;33m'
  NC='\033[0m'
  # shellcheck disable=SC2059
  printf "${YELLOW}$1${NC}\n"
}

print_success() {
  GREEN='\033[0;32m'
  NC='\033[0m'
  # shellcheck disable=SC2059
  printf "${GREEN}$1${NC}\n"
}

print_error() {
  RED='\033[0;31m'
  NC='\033[0m'
  # shellcheck disable=SC2059
  printf "${RED}$1${NC}\n"
}

print_help() {
  print_info "Usage: ./script/release.sh [OPTIONS] [Version]\n"
  print_info "  Release new version of jacoco-parser\n"
  print_info "Options:"
  print_info "  -h, --help                        Show this message and exit"
  print_info "  -v, --version                     Show the app version and exit"
  exit 0
}

options() {
  option_check_for_help
  option_check_for_version
}

option_check_for_help() {
  if [ "$OPTIONS" == "-h" ] || [ "$OPTIONS" == "--help" ]; then
    print_help
  fi
}

option_check_for_version() {
  if [ "$OPTIONS" == "-v" ] || [ "$OPTIONS" == "--version" ]; then
    print_info "Version: $APP_VERSION"
    exit 0
  fi
}

validate_new_version() {
  if [ "$NEW_VERSION" == "" ]; then
    print_help
  fi

  if [[ $NEW_VERSION =~ ^[0-9].[0-9].[0-9]$ ]]; then
    print_info "Releasing jacoco-parser v$NEW_VERSION"
  else
    print_error "Invalid version $NEW_VERSION\n"
    exit 1
  fi
}

get_current_version() {
  CURRENT_VERSION=$(grep 'version = ' build.gradle.kts | cut -d'"' -f2)
  print_info "Current jacoco-parser version is v$CURRENT_VERSION"
}

validate_current_branch_is_main() {
  local current_branch
  current_branch=$(git branch --show-current)

  if [ "$current_branch" != "main" ]; then
    print_error "\nYou are on $current_branch branch"
    print_error "Make sure your are on 'main' git branch"
    exit 1
  fi
}

update_github_workflows() {
  sed -i '' "s/$CURRENT_VERSION/$NEW_VERSION/g" .github/workflows/build.yml
  print_success "\nUpdated .github/workflows/build.yml"

  sed -i '' "s/$CURRENT_VERSION/$NEW_VERSION/g" .github/workflows/release.yml
  print_success "Updated .github/workflows/release.yml"
}

update_build_gradle() {
  sed -i '' "s/$CURRENT_VERSION/$NEW_VERSION/g" build.gradle.kts
  print_success "Updated build.gradle.kts\n"

  sed -i '' "s/version = \"$CURRENT_VERSION\"/version = \"$NEW_VERSION\"/g" src/main/kotlin/dev/jagdeepsingh/commands/JacocoParseCommand.kt
  print_success "Updated src/main/kotlin/dev/jagdeepsingh/commands/JacocoParseCommand.kt"
}

publish_new_version_to_git() {
  git add .
  git commit -m "Release v$NEW_VERSION"
  git tag "v$NEW_VERSION"
  git push origin main --tags
}

OPTIONS=$1
NEW_VERSION=$1

options

validate_new_version
validate_current_branch_is_main

get_current_version

update_github_workflows
update_build_gradle
publish_new_version_to_git
