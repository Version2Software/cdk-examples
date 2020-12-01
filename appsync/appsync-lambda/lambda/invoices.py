import json
import lineitems

def main(event, context):
    print(f"event: {event}")
    args = event["arguments"]

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

    if args != {}:
      invoices = list(filter(lambda d: d["invNumber"] == args["invNumber"], invoices))

    for invoice in invoices:
        event["arguments"] = {"invNumber": invoice["invNumber"]}
        invoice["lineItems"] = lineitems.main(event, None)

    return invoices

# e = {"arguments": {"invNumber": 100}}
# print(main(e, None))
#
# e = {"arguments": {}}
# print(main(e, None))
