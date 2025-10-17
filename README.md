# Drug Interactions API

A REST API for managing drug interaction information and analyzing adverse event signals using openFDA data.

## API Documentation

The OpenAPI 3.1 specification is available in [api-spec/openapi.yaml](api-spec/openapi.yaml).

### Key Features

- Drug interaction note management (create/update/retrieve)
- Adverse event signal analysis using openFDA data
- Input validation for drug names (3-60 characters, alphabetic + spaces/hyphens)
- Structured error responses
- Accessibility considerations built into API design

### Endpoints

- `POST /interactions` - Create or update an interaction note
- `GET /interactions?drugA&drugB` - Retrieve an interaction note
- `GET /signals?drugA&drugB&limit=50` - Analyze adverse event signals from openFDA

## Development

### API Specification

The API is defined in OpenAPI 3.1 format. To validate the specification:

1. Install a YAML validator:
```bash
npm install -g yaml-lint
```

2. Validate the spec:
```bash
yamllint api-spec/openapi.yaml
```

### Testing the API

You can use tools like Postman or curl to test the endpoints. Example calls:

```bash
# Get an interaction
curl "http://localhost:8080/interactions?drugA=Aspirin&drugB=Warfarin"

# Create an interaction
curl -X POST http://localhost:8080/interactions \
  -H "Content-Type: application/json" \
  -d '{"drugA":"Aspirin","drugB":"Warfarin","note":"May increase risk of bleeding"}'

# Get adverse event signals
curl "http://localhost:8080/signals?drugA=Aspirin&drugB=Warfarin&limit=50"
```

## Error Handling

The API uses standard HTTP status codes:
- 200/201: Success
- 400: Validation errors
- 404: Interaction not found
- 502: openFDA API errors

All error responses include:
- `code`: Machine-readable error code
- `message`: Human-readable description
- `details`: Additional context (when available)
