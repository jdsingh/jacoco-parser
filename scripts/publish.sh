#!/usr/bin/env bash

print_pwd() {
  printf "PWD: %s\n" "$(pwd)"
}

current_version() {
  CURRENT_VERSION=$(grep version ./homebrew-repo/Formula/jacoco-parser.rb | cut -d'"' -f2 | cut -d'v' -f2)
  printf "Current Version: %s\n" "$CURRENT_VERSION"
}

new_version_sha() {
  NEW_VERSION_SHA256=$(sha256sum ./homebrew-repo/jacoco-parser-"$NEW_VERSION".zip/jacoco-parser-"$NEW_VERSION".zip | cut -d ' ' -f1)
  printf "SHA256 %s of %s\n" "$NEW_VERSION_SHA256" "jacoco-parser-$NEW_VERSION.zip"
}

replace_version_and_sha() {
  sed -i "s/[0-9]\.[0.9]\.[0-9]/$NEW_VERSION/g" ./homebrew-repo/Formula/jacoco-parser.rb
  sed -i -E "s/[A-Fa-f0-9]{64}/$NEW_VERSION_SHA256/g" ./homebrew-repo/Formula/jacoco-parser.rb
}

update_homebrew_repo() {
  print_pwd
  cd ./homebrew-repo || exit
  print_pwd

  git config user.name github-actions
  git config user.email github-actions@github.com
  git checkout -b test
  git add ./Formula/jacoco-parser.rb
  git diff
  git commit -m "Update jacoco-parser to v$NEW_VERSION"
  git push origin test
}

NEW_VERSION=$1
printf "New Version: %s\n" "$NEW_VERSION"

print_pwd
current_version
new_version_sha
replace_version_and_sha
update_homebrew_repo
