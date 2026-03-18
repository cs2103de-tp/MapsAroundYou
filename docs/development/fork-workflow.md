# Fork Workflow Guide

This document describes the workflow for contributing to the MapsAroundYou project using the fork model.

## Repository Structure

```
cs2103de-tp/MapsAroundYou     # Organization (upstream/main)
        ↑
        │  Pull Requests
        │
<your-username>/MapsAroundYou # Personal fork (your GitHub account)
        ↑
        │
   Local Clone                # Local development
```

## Initial Setup

### 1. Fork the Organization Repository

1. Go to: https://github.com/cs2103de-tp/MapsAroundYou
2. Click the "Fork" button in the top-right corner
3. Select your GitHub account as the destination
4. Wait for the fork to be created

### 2. Clone Your Fork

```bash
git clone https://github.com/<your-username>/MapsAroundYou.git
cd MapsAroundYou
```

### 3. Add Upstream Remote

```bash
git remote add upstream https://github.com/cs2103de-tp/MapsAroundYou.git
```

### 4. Verify Remote Configuration

```bash
git remote -v
# Expected output:
# origin  https://github.com/<your-username>/MapsAroundYou.git (fetch)
# origin  https://github.com/<your-username>/MapsAroundYou.git (push)
# upstream        https://github.com/cs2103de-tp/MapsAroundYou.git (fetch)
# upstream        https://github.com/cs2103de-tp/MapsAroundYou.git (push)
```

## Daily Workflow

### Starting New Work

1. **Sync your fork with upstream** (recommended before starting new work):

   ```bash
   git checkout main
   git fetch upstream
   git merge upstream/main
   git push origin main
   ```

2. **Create a feature branch**:

   ```bash
   git checkout -b feature/your-feature-name
   ```

### Making Changes

1. **Make your changes** and stage them:

   ```bash
   git add .
   ```

2. **Commit with a descriptive message**:

   ```bash
   git commit -m "Description of your changes"
   ```

   Follow the [commit conventions](./git-commit-conventions.md).

3. **Push to your fork**:

   ```bash
   git push origin feature/your-feature-name
   ```

### Creating a Pull Request

1. Go to your fork on GitHub: `https://github.com/<your-username>/MapsAroundYou`
2. Click "Compare & pull request"
3. Select the base repository: `cs2103de-tp/MapsAroundYou` and branch: `main`
4. Select the head repository: `<your-username>/MapsAroundYou` and your feature branch
5. Fill in the PR title and description
6. Click "Create pull request"

### After PR Review

Once your PR is ready to merge (all conversations resolved and checks pass):
- If a reviewer has approved it, they may merge it, OR
- You can merge it yourself once the branch is up to date with main

> **Note:** Reviewer approval is recommended but not enforced. The required `PR Quality Gate` check must pass and all conversations
> must be resolved before merging.

## Syncing Your Fork

To keep your local fork in sync with the upstream organization:

```bash
# Fetch the latest from upstream
git fetch upstream

# Update your main branch
git checkout main
git merge upstream/main

# Push to your fork
git push origin main

# Update your feature branch if needed
git checkout feature/your-feature
git rebase main
```

## Important Notes

- **Never push directly to the organization's main branch** - it is protected
- **Always create a PR** for any changes you want to merge
- **Wait for the required `PR Quality Gate` check to pass** before merging
- **We recommend getting at least 1 approval** before merging
- **Keep your fork updated** by regularly syncing with upstream
- **Resolve all conversations** in the PR before it can be merged

## Branch Protection Rules

The organization's `main` branch has these protection rules:
- Required status check: `PR Quality Gate` must pass before merge
- Approving review: recommended (not enforced by branch protection)
- Protection applies to administrators (no bypass)
- Dismiss stale reviews when new commits are pushed
- Require conversation resolution before merging

## Quick Reference

| Action | Command |
|--------|---------|
| Clone fork | `git clone https://github.com/<your-username>/MapsAroundYou.git` |
| Add upstream | `git remote add upstream https://github.com/cs2103de-tp/MapsAroundYou.git` |
| Create branch | `git checkout -b feature/name` |
| Push to fork | `git push origin feature/name` |
| Sync with upstream | `git fetch upstream && git merge upstream/main` |
