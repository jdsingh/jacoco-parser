# jacoco-parser

JaCoCo xml report parser written in Kotlin.

`jacoco-parser` can print output in JSON or Text (cut friendly, check examples below).

## Build

`./gradlew installDist`

> Create alias for convenience
>
> `alias jacoco-parser='{path-to-bin}/bin/jacoco-parser'`

## Usage

`jacoco-parser report.xml`

Try `jacoco-parser --help`

## Text

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

### Print report of single attribute

```bash
jacoco-parser report.xml | grep BRANCH
```

```text
Type: BRANCH, Covered: 185, Missed: 287, Total: 472, Coverage: 39.19
```

### Print coverage of single attribute

```bash
jacoco-parser report.xml | grep BRANCH | cut -d',' -f5
```

```text
Coverage: 39.19
```

### Print only coverage number of single attribute

```bash
jacoco-parser report.xml | grep BRANCH | cut -d',' -f5 | cut -d':' -f2
```

```text
39.19
```

## JSON

> Tip: You can use [jq](https://stedolan.github.io/jq/) to extract data you're interested in from output json.

```bash
jacoco-parser report.xml --output JSON | jq .
```

```json
{
  "module_name": "feature-payment-detail",
  "instruction": {
    "type": "INSTRUCTION",
    "covered": 3166,
    "missed": 4665,
    "total": 7831,
    "coverage": 40.42,
    "coverage_text": "40.43"
  },
  "branch": {
    "type": "BRANCH",
    "covered": 185,
    "missed": 287,
    "total": 472,
    "coverage": 39.19,
    "coverage_text": "39.19"
  },
  "line": {
    "type": "LINE",
    "covered": 435,
    "missed": 714,
    "total": 1149,
    "coverage": 37.85,
    "coverage_text": "37.86"
  },
  "complexity": {
    "type": "COMPLEXITY",
    "covered": 149,
    "missed": 459,
    "total": 608,
    "coverage": 24.50,
    "coverage_text": "24.51"
  },
  "method": {
    "type": "METHOD",
    "covered": 112,
    "missed": 255,
    "total": 367,
    "coverage": 30.51,
    "coverage_text": "30.52"
  },
  "class": {
    "type": "CLASS",
    "covered": 43,
    "missed": 92,
    "total": 135,
    "coverage": 31.85,
    "coverage_text": "31.85"
  }
}
```

### Using jq

```bash
jacoco-parser report.xml --output JSON | jq '{name: .module_name, coverage: .instruction.coverage_text}'
```

```json
{
  "name": "feature-payment-detail",
  "coverage": "40.43"
}
```
