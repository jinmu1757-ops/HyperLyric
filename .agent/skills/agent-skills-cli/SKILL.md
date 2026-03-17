---
name: agent-skills-cli
description: CLI tool for the open agent skills ecosystem. Allows managing, finding, and installing other skills using Vercel's skills CLI (https://skills.sh).
---

# Agent Skills Ecosystem CLI

This skill allows you to use the `@vercel/skills` CLI tool to seamlessly install, remove, find, list, and update other agent skills.

## When to Use

Activate this skill when the user asks to:
- Install a new agent skill from a repository
- List their currently installed skills
- Remove an existing skill
- Search the `skills.sh` registry for skills matching a keyword
- Update all installed agent skills to their latest versions

## Available Commands

Whenever instructed to manage a skill, use your standard terminal tool (`run_command`) to run the appropriate `npx @vercel/skills` command. 

### 1. Installing a Skill
To install skills from a GitHub repo, local directory, or URL:
```bash
npx @vercel/skills add <source> -y --agent antigravity
```
- `<source>` is the GitHub shorthand (e.g., `vercel-labs/agent-skills`) or a full URL.
- **IMPORTANT**: Always include `-y` to bypass confirmation prompts.
- **IMPORTANT**: Always append `--agent antigravity` to ensure the skill is placed in `.agent/skills/`.

### 2. Listing Skills
To show installed skills for this specific agent:
```bash
npx @vercel/skills list --agent antigravity
```

### 3. Searching for a Skill
To query the ecosystem for a specific term:
```bash
npx @vercel/skills find <keyword>
```

### 4. Removing a Skill
To delete a skill without manual prompts:
```bash
npx @vercel/skills remove <skill-name> -y --agent antigravity
```

### 5. Updating Skills
To check for updates and update all installed skills automatically:
```bash
npx @vercel/skills update
```

## Important Notes
- Always run these from the project root directory.
- Avoid manually tweaking `.agent/skills` folders when using the CLI; allow the tool to handle symlinking and copying.
- If you need to specify a certain skill from a multi-skill repo, use `--skill <skill_name>`.
