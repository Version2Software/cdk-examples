invoices = [{
           "invNumber": 100,
           "custId": "AAA",
           "purchaseDate": "2020-11-23"
       },
       {
           "invNumber": 101,
           "custId": "BBB",
           "purchaseDate": "2020-11-24"
       },
       {
           "invNumber": 102,
           "custId": "CCC",
           "purchaseDate": "2020-11-24"
       }]

def main(event, context):
    print(f"event: {event}")
    args = event["arguments"]

    if args != {}:
      return list(filter(lambda d: d["invNumber"] == args["invNumber"], invoices))

    return invoices

# Test
# e = {"arguments": {"invNumber": 100}}
# print(main(e, None))
#
# e = {"arguments": {}}
# print(main(e, None))
