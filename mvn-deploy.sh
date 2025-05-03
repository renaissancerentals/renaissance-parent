#!/bin/bash

set -euo pipefail

# Step 1: Verify the build
echo "🛠️ Verifying build with quality profile..."
mvn -B verify -Pquality -Dgpg.skip=true

# Step 2: Bump minor version
echo "🔢 Bumping minor version..."
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"
NEW_VERSION="${MAJOR}.$((MINOR + 1)).0"

mvn --batch-mode versions:set -DnewVersion=$NEW_VERSION
mvn versions:commit

echo "✅ New version set: $NEW_VERSION"

# Step 3: Commit and push version bump
echo "📦 Committing version bump..."

git commit -am "chore(release): bump to version $NEW_VERSION"
git push origin main

# Step 4: Deploy to Sonatype
echo "🚀 Deploying to Sonatype..."
mvn deploy -DskipTests

echo "🎉 Release complete: $NEW_VERSION"
