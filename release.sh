#!/usr/bin/env bash

NEW_VERSION=$1
CURRENT_VERSION=$(cat build.gradle.kts | grep 'version = ' | cut -d'"' -f2)

echo "Current Version: $NEW_VERSION"
echo "New Version: $CURRENT_VERSION"

git checkout main

sed -i '' "s/$CURRENT_VERSION/$NEW_VERSION/g" .github/workflows/build.yml
sed -i '' "s/$CURRENT_VERSION/$NEW_VERSION/g" .github/workflows/release.yml
sed -i '' "s/$CURRENT_VERSION/$NEW_VERSION/g" build.gradle.kts

git add build.gradle.kts
git add .github/workflows/release.yml
git add .github/workflows/build.yml
git commit -m "Release v$NEW_VERSION"
git tag "v$NEW_VERSION"
git push origin main --tags
