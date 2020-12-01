import json

def main(event, context):
    print(f'event: {event}')
    args = event['arguments']

    line_items = [{
               "invNumber": 100,
               "partNumber": "TU100",
               "description": "TacUmbrella",
               "price": 9.99
           },
           {
               "invNumber": 100,
               "partNumber": "TLB9000",
               "description": "TacLunchbox 9000",
               "price": 19.99
           },
           {
               "invNumber": 101,
               "partNumber": "TPJ5",
               "description": "TacPajamas",
               "price": 99.99
           }]

    if args == {}:
        return line_items
    else:
        return list(filter(lambda line_item: line_item['invNumber'] == args['invNumber'], line_items))
