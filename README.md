# jacoco-parser [![Build jacoco-parser](https://github.com/jdsingh/jacoco-parser/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/jdsingh/jacoco-parser/actions/workflows/build.yml)

JaCoCo xml report parser written in Kotlin.

`jacoco-parser` can print output in JSON or Text (grep, cut friendly, check examples below).

## Install

`brew install jdsingh/repo/jacoco-parser`

## Usage

`jacoco-parser report.xml`

Try `jacoco-parser --help`

## Output

### Default

```bash
jacoco-parser report.xml
```

```text
Module: feature-payment-detail
Type: INSTRUCTION, Covered: 3166, Missed: 4665, Total: 7831, Coverage: 40.43
Type: BRANCH, Covered: 185, Missed: 287, Total: 472, Coverage: 39.19
Type: LINE, Covered: 435, Missed: 714, Total: 1149, Coverage: 37.86
Type: COMPLEXITY, Covered: 149, Missed: 459, Total: 608, Coverage: 24.51
Type: METHOD, Covered: 112, Missed: 255, Total: 367, Coverage: 30.52
Type: CLASS, Covered: 43, Missed: 92, Total: 135, Coverage: 31.85
```

**Short Format (One line)**
```bash
jacoco-parser --short report.xml
```

```text
Module: feature-payment-detail, Type: INSTRUCTION, Coverage: 40.43
```

### JSON

> Tip: You can use [jq](https://stedolan.github.io/jq/) to extract data you're interested in from output json.

```bash
jacoco-parser --output JSON report.xml
```

```json
{
  "module_name":"feature-payment-detail",
  "instruction":{"type":"INSTRUCTION","covered":3166,"missed":4665,"total":7831,"coverage":40.429063976503635,"coverage_text":"40.43"},
  "branch":{"type":"BRANCH","covered":185,"missed":287,"total":472,"coverage":39.19491525423729,"coverage_text":"39.19"},
  "line":{"type":"LINE","covered":435,"missed":714,"total":1149,"coverage":37.85900783289817,"coverage_text":"37.86"},
  "complexity":{"type":"COMPLEXITY","covered":149,"missed":459,"total":608,"coverage":24.50657894736842,"coverage_text":"24.51"},
  "method":{"type":"METHOD","covered":112,"missed":255,"total":367,"coverage":30.517711171662125,"coverage_text":"30.52"},
  "class":{"type":"CLASS","covered":43,"missed":92,"total":135,"coverage":31.851851851851855,"coverage_text":"31.85"}
}
```
