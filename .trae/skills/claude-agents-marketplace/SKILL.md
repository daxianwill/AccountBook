---
name: "claude-agents-marketplace"
description: "Claude Code 智能自动化和多代理编排系统，提供 72 个专注插件、112 个专业代理和 146 个代理技能。在需要软件开发自动化、多代理协作或专业领域技能时调用。"
---

# Claude Agents Marketplace

Claude Code 智能自动化和多代理编排系统的综合生产就绪解决方案。

## 概述

本 skill 提供了一个统一的仓库，包含现代软件开发所需的一切：

- **72 个专注插件** - 细粒度、单一用途的插件，优化为最小 token 使用和可组合性
- **112 个专业代理** - 架构、语言、基础设施、质量、数据/AI、文档、业务运营和 SEO 等领域的深度专家
- **146 个代理技能** - 模块化知识包，支持渐进式披露，提供专业知识
- **16 个工作流编排器** - 多代理协调系统，用于全栈开发、安全加固、ML 流水线和事件响应等复杂操作
- **79 个开发工具** - 优化的实用程序，包括项目脚手架、安全扫描、测试自动化和基础设施设置

## 关键特性

### 细粒度插件架构
- 72 个专注插件，优化为最小 token 使用
- 平均每个插件 3.4 个组件（遵循 Anthropic 的 2-8 模式）
- 完全隔离，每个插件都有自己的代理、命令和技能

### 渐进式披露（技能）
三层架构实现 token 效率：
1. **元数据** - 名称和激活条件（始终加载）
2. **指令** - 核心指导（激活时加载）
3. **资源** - 示例和模板（按需加载）

## 三层模型策略

| 层级 | 模型 | 代理数量 | 用例 |
|------|------|----------|------|
| Tier 1 | Opus 4.6 | 42 | 关键架构、安全性、所有代码审查、生产编码 |
| Tier 2 | 继承 | 42 | 复杂任务 - 用户选择模型（AI/ML、后端、前端/移动端） |
| Tier 3 | Sonnet | 51 | 智能支持（文档、测试、调试、网络、API 文档） |
| Tier 4 | Haiku | 18 | 快速操作任务（SEO、部署、简单文档） |

## 插件类别（24 类，72 插件）

| 类别 | 插件数量 | 描述 |
|------|----------|------|
| 🎨 开发 | 4 | 调试、后端、前端、多平台 |
| 📚 文档 | 3 | 代码文档、API 规范、图表、C4 架构 |
| 🔄 工作流 | 5 | Git、全栈、TDD、Conductor（上下文驱动开发）、Agent Teams（多代理编排） |
| ✅ 测试 | 2 | 单元测试、TDD 工作流 |
| 🔍 质量 | 2 | 综合审查、性能 |
| 🤖 AI & ML | 4 | LLM 应用、代理编排、上下文、MLOps |
| 📊 数据 | 2 | 数据工程、数据验证 |
| 🗄️ 数据库 | 2 | 数据库设计、迁移 |
| 🚨 运维 | 4 | 事件响应、诊断、分布式调试、可观测性 |
| ⚡ 性能 | 2 | 应用性能、数据库/云优化 |
| ☁️ 基础设施 | 5 | 部署、验证、Kubernetes、云、CI/CD |
| 🔒 安全 | 4 | 扫描、合规、后端/API、前端/移动端 |
| 💻 语言 | 7 | Python、JS/TS、系统、JVM、脚本、函数式、嵌入式 |
| 🔗 区块链 | 1 | 智能合约、DeFi、Web3 |

## 热门用例

### 全栈功能开发
```
/full-stack-orchestration:full-stack-feature "用户认证与 OAuth2"
```
协调 7+ 个代理：后端架构师 → 数据库架构师 → 前端开发者 → 测试自动化工程师 → 安全审计员 → 部署工程师 → 可观测性工程师

### Python 开发（现代工具）
```
/python-development:python-scaffold fastapi-microservice
```
创建生产就绪的 FastAPI 项目，包含异步模式，自动激活技能：
- async-python-patterns - AsyncIO 和并发
- python-testing-patterns - pytest 和 fixtures
- uv-package-manager - 快速依赖管理

### Kubernetes 部署
```
"创建带 Helm 图表和 GitOps 的生产级 Kubernetes 部署"
```
使用 kubernetes-architect 代理和 4 个专业技能创建生产级配置

### 安全加固
```
/security-scanning:security-hardening --level comprehensive
```
多代理安全评估，包括 SAST、依赖扫描和代码审查

## 常用插件

| 插件 | 代理 | 技能数 |
|------|------|--------|
| comprehensive-review | architect-review, code-reviewer, security-auditor | - |
| javascript-typescript | javascript-pro, typescript-pro | 4 |
| python-development | python-pro, django-pro, fastapi-pro | 16 |
| backend-development | backend-architect | 3 |
| kubernetes-operations | kubernetes-architect | 4 |
| security-scanning | security-auditor | - |
| full-stack-orchestration | 多代理协调 | - |
| agent-teams | 团队代理 | 6 |
| conductor | 项目管理 | 3 |

## 使用方式

在 Trae IDE 中，当需要以下场景时可使用本 skill：

1. **项目脚手架** - 创建各种语言和框架的生产就绪项目结构
2. **代码审查** - 多视角代码质量和安全性审查
3. **架构设计** - 系统架构和设计模式建议
4. **安全审计** - OWASP 合规性和漏洞扫描
5. **测试自动化** - 单元测试、集成测试和 TDD 工作流
6. **部署配置** - Kubernetes、云基础设施和 CI/CD 流水线
7. **多代理协作** - 复杂任务的多专家并行处理

## 激活技能

当上下文涉及以下内容时，本 skill 自动激活：
- 软件开发自动化需求
- 多代理协作场景
- 特定领域的专业知识需求
- Claude Code 生态系统相关查询
