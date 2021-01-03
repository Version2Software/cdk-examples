line_items = [{
           "invNumber": 100,
           "lineNumber": 1,
           "partNumber": "TU100",
           "description": "TacUmbrella",
           "price": 9.99
       },
       {
           "invNumber": 100,
           "lineNumber": 2,
           "partNumber": "TLB9000",
           "description": "TacLunchbox 9000",
           "price": 19.99
       },
       {
           "invNumber": 101,
           "lineNumber": 1,
           "partNumber": "TPJ5",
           "description": "TacPajamas",
           "price": 99.99
       }]

def main(event, context):
    print(f'event: {event}')
    source = event['source']

    return list(filter(lambda line_item: line_item['invNumber'] == source['invNumber'], line_items))

# Test
# e = {"source": {"invNumber": 100}}
# print(main(e, None))