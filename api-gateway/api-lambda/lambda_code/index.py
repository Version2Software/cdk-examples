import json

def main(event, context):
    print(f'event: {event}')

    #proxy
    if "resource" in event.keys():
        status = event["path"].lstrip('/proxy/')

        return {
           "statusCode": int(status),
           "body": json.dumps(event)
        }

    # nonproxy
    else:
        if event["status"] == "200":
            return event
        elif event["status"] == "400":
            raise Exception("400")
        elif event["status"] == "500":
            raise Exception("500")
        else:
            raise Exception("502")
