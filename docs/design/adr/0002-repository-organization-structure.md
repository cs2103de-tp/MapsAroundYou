# ADR 0002: Repository Organization Structure - Upstream/Fork Model

## Status

Accepted

## Date

2026-03-03

## Context

The MapsAroundYou project was initially developed under a personal GitHub account. To support proper code review practices and team collaboration, the project was moved to an organization with the following requirements:
- The organization owns the main/upstream repository
- Individual contributors work on personal forks
- All changes to the organization's main branch must go through pull requests

## Decision

The repository structure has been changed to:

```
Organization: cs2103de-tp
├── cs2103de-tp/MapsAroundYou (upstream/main - organization owned)
│
└── <username>/MapsAroundYou (fork - personal account)
    └── Local: <local-clone-path>
```

### Git Remote Configuration

| Remote | URL | Purpose |
|--------|-----|---------|
| origin | https://github.com/<username>/MapsAroundYou.git | Personal fork (push/pull) |
| upstream | https://github.com/cs2103de-tp/MapsAroundYou.git | Organization upstream (fetch/pull) |

### Branch Protection Rules

The organization's `main` branch has the following protection rules:

- **Require pull request reviews before merging**: We recommend at least 1 reviewer
- **Include administrators**: Protection rules apply to admins (no bypass)
- **Dismiss stale reviews**: When new commits are pushed
- **Require conversation resolution**: All conversations must be resolved before merge
- **Lock branch**: Prevents force pushes to main

Personal forks have **no branch protection rules**, allowing direct pushes for development.

## Consequences

### Positive

- All changes to the organization's main branch require a pull request (code review is recommended best practice)
- Clear separation between personal development and official codebase
- Contributors can work independently on their forks
- Audit trail through PR history

### Team Workflow

1. **Fork**: Each team member forks the organization's repository to their account
2. **Clone**: Clone your fork locally
3. **Add upstream**: Add the organization repository as `upstream` remote
4. **Create feature branch**: Create a branch for your work
5. **Make changes and commit**: Follow commit conventions
6. **Push to your fork**: Push your branch to your personal fork
7. **Create PR**: Open a pull request from your fork to the organization
8. **Review**: We recommend waiting for at least 1 reviewer approval before merging
9. **Merge**: After approval, the PR will be merged to organization's main

### Syncing with Upstream

To keep your fork up to date with the organization's main branch:

```bash
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

## Related Documents

- [Branch Protection ADR](./0001-enforce-branch-protection-for-all.md)
- [Development Workflow](../../development/git-commit-conventions.md)
- [Fork Workflow Guide](../../development/fork-workflow.md)
