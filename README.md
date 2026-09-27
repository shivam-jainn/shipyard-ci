# Shipyard CI ⚓

<p align="center">
  <strong>Enterprise CI/CD Pipelines, GitHub Actions, Argo Workflows, and Jenkins Integrations for Shipyard Agent Evaluations.</strong>
</p>

<p align="center">
  <a href="https://github.com/shivam-jainn/shipyard-ci/actions"><img src="https://img.shields.io/badge/CI-Argo%20%7C%20GHA%20%7C%20Jenkins-blueviolet.svg" alt="CI Engines"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Proprietary-red.svg" alt="License: Proprietary"></a>
</p>

---

## What is Shipyard CI?

`shipyard-ci` turns AI agent evaluation from exploratory local "vibe checks" into an automated, deterministic **blocking gate in your pull requests and delivery pipelines**.

When an autonomous coding or workflow agent regresses on prompt instructions, leaks sensitive variables, or fails deterministic Python scoring rubrics, `shipyard-ci` fails the pipeline and blocks merge.

---

## Supported CI / CD Platforms

| Platform | Location | Description |
| :--- | :--- | :--- |
| **GitHub Actions** | [`action.yml`](action.yml) | Composite action with automated PR markdown comment summaries. |
| **Argo Workflows (WFT)** | [`argo/workflow-template.yaml`](argo/workflow-template.yaml) | Kubernetes-native `WorkflowTemplate` & `CronWorkflow` with S3 rollout archiving. |
| **Jenkins** | [`jenkins/Jenkinsfile`](jenkins/Jenkinsfile) | Declarative multi-stage pipeline + Groovy Shared Library. |
| **GitLab CI** | [`gitlab/.gitlab-ci-shipyard.yml`](gitlab/.gitlab-ci-shipyard.yml) | Docker-in-Docker GitLab runner template with rollout caching. |

---

## 1. GitHub Actions Quickstart

Add `.github/workflows/agent-gate.yml` to your repository:

```yaml
name: Agent Regression Gate

on:
  pull_request:
    branches: [main]

jobs:
  shipyard:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      pull-requests: write

    steps:
      - uses: actions/checkout@v4

      - name: Run Shipyard Gate
        uses: shivam-jainn/shipyard-ci@v1
        with:
          path: 'evalset/'
          post-comment: 'true'
        env:
          OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
          ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}

      - name: Archive Rollout Trajectories
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: atif-trajectories
          path: evalset/rollouts/
```

---

## 2. Argo Workflows (Kubernetes WFT)

Deploy the `WorkflowTemplate` to your Kubernetes cluster:

```bash
kubectl apply -f argo/workflow-template.yaml
kubectl apply -f argo/cron-workflow.yaml
```

Trigger an ad-hoc run using the Argo CLI:

```bash
argo submit --from wft/shipyard-eval-runner \
  -p eval-path="evalset/order-bot" \
  -p agent-name="claude-code" \
  -p git-repo="https://github.com/my-org/agents.git"
```

---

## 3. Jenkins Pipeline

In your Jenkins Pipeline job or multibranch pipeline, point to [`jenkins/Jenkinsfile`](jenkins/Jenkinsfile), or import the shared library step:

```groovy
@Library('shipyard-shared-library') _

pipeline {
    agent any
    stages {
        stage('Eval') {
            steps {
                shipyardEval(evalPath: 'evalset/', agent: 'claude-code', ci: true)
            }
        }
    }
}
```

---

## Trajectory Rollout Artifacts

Every CI run captures complete **Agent Trajectory Interchange Format (ATIF)** JSON logs, tracking:
- Step-by-step tool invocations (bash, edits, web search).
- Model latency and token usage.
- Itemized Python rubric criteria scores.

---

## License

**Proprietary — all rights reserved.** This repository is not open source and is not licensed under any open source license.

You may **not** copy, reproduce, redistribute, sublicense, publish, fork, mirror, or create derivative works of this software without prior written authorization. See [LICENSE](LICENSE) for the full terms.
