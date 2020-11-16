import json

def main(event, context):
    print(event)
    event["requestContext"]["accountId"] = "REDACTED"

    return {
        'statusCode': 200,
        'body': json.dumps(event)
    }
