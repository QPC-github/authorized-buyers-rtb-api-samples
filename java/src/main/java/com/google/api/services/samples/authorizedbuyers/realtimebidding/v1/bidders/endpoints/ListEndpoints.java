/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.api.services.samples.authorizedbuyers.realtimebidding.v1.bidders.endpoints;

import com.google.api.services.realtimebidding.v1.RealTimeBidding;
import com.google.api.services.realtimebidding.v1.model.Endpoint;
import com.google.api.services.realtimebidding.v1.model.ListEndpointsResponse;
import com.google.api.services.samples.authorizedbuyers.realtimebidding.Utils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/** This sample illustrates how to list endpoints for the given bidder's account ID. */
public class ListEndpoints {

  public static void execute(RealTimeBidding client, Namespace parsedArgs) throws IOException {
    Integer accountId = parsedArgs.getInt("account_id");
    String parent = String.format("bidders/%s", accountId);
    Integer pageSize = parsedArgs.getInt("page_size");
    String pageToken = null;

    System.out.printf("Listing endpoints for bidder account '%s':\n", accountId);

    do {
      List<Endpoint> endpoints = null;

      ListEndpointsResponse response =
          client
              .bidders()
              .endpoints()
              .list(parent)
              .setPageSize(pageSize)
              .setPageToken(pageToken)
              .execute();

      endpoints = response.getEndpoints();
      pageToken = response.getNextPageToken();

      if (endpoints == null) {
        System.out.println("No endpoints found.");
      } else {
        for (Endpoint endpoint : endpoints) {
          Utils.printEndpoint(endpoint);
        }
      }
    } while (pageToken != null);
  }

  public static void main(String[] args) {
    ArgumentParser parser =
        ArgumentParsers.newFor("ListBidders")
            .build()
            .defaultHelp(true)
            .description("Lists endpoints for the given bidder account.");
    parser
        .addArgument("-a", "--account_id")
        .help(
            "The resource ID of the bidders resource under which the endpoint exists. This will be"
                + " used to construct the name used as a path parameter for the endpoints.list "
                + "request.")
        .required(true)
        .type(Integer.class);
    parser
        .addArgument("-p", "--page_size")
        .help(
            "The number of rows to return per page. The server may return fewer rows than "
                + "specified.")
        .setDefault(Utils.getMaximumPageSize())
        .type(Integer.class);

    Namespace parsedArgs = null;
    try {
      parsedArgs = parser.parseArgs(args);
    } catch (ArgumentParserException ex) {
      parser.handleError(ex);
      System.exit(1);
    }

    RealTimeBidding client = null;
    try {
      client = Utils.getRealTimeBiddingClient();
    } catch (IOException ex) {
      System.out.printf("Unable to create RealTimeBidding API service:\n%s", ex);
      System.out.println("Did you specify a valid path to a service account key file?");
      System.exit(1);
    } catch (GeneralSecurityException ex) {
      System.out.printf("Unable to establish secure HttpTransport:\n%s", ex);
      System.exit(1);
    }

    try {
      execute(client, parsedArgs);
    } catch (IOException ex) {
      System.out.printf("RealTimeBidding API returned error response:\n%s", ex);
      System.exit(1);
    }
  }
}
