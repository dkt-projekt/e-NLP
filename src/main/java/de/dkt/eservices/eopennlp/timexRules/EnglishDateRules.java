package de.dkt.eservices.eopennlp.timexRules;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.dkt.eservices.eopennlp.modules.RegexFinder;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.util.Span;

public class EnglishDateRules {
	
	static HashMap<Integer, String> englishDateRegexMap = new HashMap<Integer, String>();
	static HashMap<String, Integer> englishMonthName2Number = new HashMap<String, Integer>(){{
		put("january", 1);
		put("february", 2);
		put("march", 3);
		put("april", 4);
		put("may", 5);
		put("june", 6);
		put("july", 7);
		put("august", 8);
		put("september", 9);
		put("october", 10);
		put("november", 11);
		put("december", 12);
	}};
	
	static HashMap<String, Integer> englishDayName2Integer = new HashMap<String, Integer>(){{
		put("sunday", 1);
		put("monday", 2);
		put("tuesday", 3);
		put("thursday", 4);
		put("wednesday", 5);
		put("friday", 6);
		put("saturday", 7);
	}};

	
	public static RegexNameFinder initEnglishDateFinder(){

		final String monthName = "(?i)(january|february|march|april|may|june|july|august|september|october|november|december)";
		final String monthNumber = "(0?[0-9]|1[0-2])";
		
		final String time = "(([0-9]|0[0-9]|1[0-2])(:|.)?([0-5][0-9])?)";
		
		final String dayName = "(?i)(monday|tuesday|wednesday|thursday|friday|saturday|sunday)";
		final String dayNumber = "(0?[1-9]|1[0-9]|2[0-9]|3[0-1])";
		final String season = "(?i)(summer|winter|spring|autumn|fall)";
		
		final String yearNumber = "([1-2]\\d{3})";
		
		final String beginningMiddleEnd = "(?i)(start|beginning|middle|end)";
		final String beforeChrist = "(?i)\\d{1,4} b\\.?c\\.?";
		
		final String alphaNumber = "(?i)(one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|twenty-one|twenty-two|twenty-three|twenty-four|twenty-five|twenty-six|twenty-seven|twenty-eight|twenty-nine|thirty|thirty-one|thirty-two|thirty-three|thirty-four|thirty-five|thirty-six|thirty-seven|thirty-eight|thirty-nine|forty|forty-one|forty-two|forty-three|forty-four|forty-five|forty-six|forty-seven|forty-eight|forty-nine|fifty|fifty-one|fifty-two|fifty-three|fifty-four|fifty-five|fifty-six|fifty-seven|fifty-eight|fifty-nine|sixty|sixty-one|sixty-two|sixty-three|sixty-four|sixty-five|sixty-six|sixty-seven|sixty-eight|sixty-nine|seventy|seventy-one|seventy-two|seventy-three|seventy-four|seventy-five|seventy-six|seventy-seven|seventy-eight|seventy-nine|eighty|eighty-one|eighty-two|eighty-three|eighty-four|eighty-five|eighty-six|eighty-seven|eighty-eight|eighty-nine|ninety|ninety-one|ninety-two|ninety-three|ninety-four|ninety-five|ninety-six|ninety-seven|ninety-eight|ninety-nine|(one )?hundred)";
		final String day = "(?i)days?";
		final String week = "(?i)weeks?";
		final String month = "(?i)months?";
		final String year = "(?i)years?";
		
		final String holidays = "(?i)(christmas( day)?|easter( monday)?|new year'?s day|may day|boxing day|independence day|labor day|memorial day|veteran'?s day|thanksgiving)";
		
		
		englishDateRegexMap.put(1, String.format("\\b%s %s(,? %s)?\\b", dayNumber, monthName, yearNumber)); //automated
		englishDateRegexMap.put(2, String.format("\\b%s( %s(st|nd|rd|th)?)?,?( %s)?\\b", monthName, dayNumber, yearNumber)); // automated
		englishDateRegexMap.put(3, String.format("\\b%s\\b", yearNumber)); //automated
		englishDateRegexMap.put(4, String.format("\\b%s[/\\-\\.]%s([/\\-\\.]%s)?\\b", dayNumber, monthNumber, yearNumber)); //automated
		englishDateRegexMap.put(5, String.format("\\bmid(\\-| )%s\\b", yearNumber));//automated
		englishDateRegexMap.put(6, String.format("\\bmid(\\-| )%s\\b", monthName));//automated
		englishDateRegexMap.put(7, String.format("\\b%s( (of )?%s)?\\b", season, yearNumber));//automated
		englishDateRegexMap.put(8, String.format("\\b%s of %s\\b", beginningMiddleEnd, yearNumber));//automated
		englishDateRegexMap.put(9, String.format("\\b%s of %s\\b", beginningMiddleEnd, monthName));//automated
		englishDateRegexMap.put(10, String.format("\\bthe %s(st|nd|rd|th)\\b", dayNumber)); //automated TODO: debug for/also allow superscript for th?
		englishDateRegexMap.put(11, String.format("\\b%s", beforeChrist));//automated
		englishDateRegexMap.put(12, String.format("\\b(early|late) %s\\b", yearNumber));//automated
		englishDateRegexMap.put(13, String.format("\\b(early|late) %s\\b", monthName));//automated
		englishDateRegexMap.put(14, String.format("(?i)\\b(today|(right )?now|this (day|date))\\b"));//automated
		englishDateRegexMap.put(15, String.format("(?i)\\b%s (%s|%s|%s|%s) (later|after|earlier|before)\\b", alphaNumber, day, week, month, year));//automated
		englishDateRegexMap.put(16, String.format("\\b%ss\\b", yearNumber)); //automated
		englishDateRegexMap.put(17, String.format("\\b%s\\b", dayName));
		englishDateRegexMap.put(18, String.format("\\b%s( %s)?\\b", holidays, yearNumber));//automated
		englishDateRegexMap.put(19, String.format("\\b%s (?i)(a.m|p.m)\\b", time));//automated
		
		Pattern[] regexes = {
			Pattern.compile(englishDateRegexMap.get(1)),
			Pattern.compile(englishDateRegexMap.get(2)),
			Pattern.compile(englishDateRegexMap.get(3)),
			Pattern.compile(englishDateRegexMap.get(4)),
			Pattern.compile(englishDateRegexMap.get(5)),
			Pattern.compile(englishDateRegexMap.get(6)),
			Pattern.compile(englishDateRegexMap.get(7)),
			Pattern.compile(englishDateRegexMap.get(8)),
			Pattern.compile(englishDateRegexMap.get(9)),
			Pattern.compile(englishDateRegexMap.get(10)),
			Pattern.compile(englishDateRegexMap.get(11)),
			Pattern.compile(englishDateRegexMap.get(12)),
			Pattern.compile(englishDateRegexMap.get(13)),
			Pattern.compile(englishDateRegexMap.get(14)),
			Pattern.compile(englishDateRegexMap.get(15)),
			Pattern.compile(englishDateRegexMap.get(16)),
			Pattern.compile(englishDateRegexMap.get(17)),
			Pattern.compile(englishDateRegexMap.get(18)),
			Pattern.compile(englishDateRegexMap.get(19)),
		};
	
		RegexNameFinder rnf = new RegexNameFinder(regexes, null);
		return rnf;

	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		PrintStream out = new PrintStream(new FileOutputStream("outputManual.txt"));
  	  System.setOut(out);
  	  
        RegexNameFinder timeFinder= EnglishDateRules.initEnglishDateFinder();
        String input = "0:49 autoplay autoplay Copy this code to your website or blog MOSCOW — Timur the goat was meant to be nothing more than a tasty meal for Amur, a Siberian tiger living in a safari park. 08:35 GMT - Roadmap signed - The head of the UN atomic watchdog says Iran has signed a  roadmap  for probing suspected efforts to develop nuclear weapons, a key part of an overall accord with major powers. 100 moments from the Iraq War 100 photos A boy stands at the scene of a car bombing in front of the Shaheen Hotel in Baghdad on January 28, 2004. 10 a.m. From cyberspace to space itself, Republican presidential candidate Ben Carson says  strength is really the defense against aggressiveness by others  from cyberspace to space itself. 11 hours Meet the Press With 16 Million in Obamacare, Is the Repeal Debate Over? 12 photos: Unveiled: Afghan women past and present Afghanistan: In the present – Women wait to receive food aid during a U.N. World Food Program scheme in Kabul in December 2001. 13.54 An unconfirmed report emerges that the hostage-taker at the supermarket is the person police were looking for in connection with the killing of a policewoman in a southern Paris suburb on Thursday. 15-M22M1769, 1770 Sarah Cooper has been charged with improper use of registration, no registration & no insurance offenses, as of 7-29 & 7-26-15, respectively. 16 January 2015 Last updated at 09:55 Why would anyone want an eyeball tattoo? 16 photos: Transgender identity in the news Born female, Brandon Teena was living as a man in Nebraska when he was raped and killed by two men in 1993. 16 photos: X-Men characters X-Men characters – Halle Berry reprises her role as one of the X-Men's most ubiquitous team members, Storm. 17 photos: Meet the faces of the new 'Star Wars' Obviously, you can't have Daniels' C-3PO without Kenny Baker's R2-D2. 1974 - Serves as the Democratic National Committee campaign chairman for the 1974 congressional elections. 1976 The Olympic torch is ignited during the Montreal 1976 Olympic Games opening ceremony. 1991 — The Republic of China applies to join the U.N. separately from mainland China as the representative of Taiwan and its related islands, saying that Resolution 2758 was irrelevant to Taipei's status. 19 Action News compiled the all of the documents, which can be viewed below. 1. Are you concerned about the direction this country is headed? 2014's AirAsia crash will continue to affect travel in 2016. 2015 'Dancing With the Stars' contestants 13 photos Legendary soul singer Patti LaBelle, who was partnered with pro dancer Artem Chigvintsev, will always be a winner in the music world. 2015 Model-Year Vehicle To discuss this topic, or any other automotive-related information, with a Kelley Blue Book analyst on-camera via the company's on-site studio, please contact a member of the Public Relations team to book an interview. 21inc (Note: CBC does not endorse and is not responsible for the content of external links.) Two Moncton men are lobbying municipal councils in New Brunswick for support to allow permanent residents of the province to vote in municipal elections. 21 photos: Chris Christie's career in photos Christie speaks at a Reform Agenda Town Hall meeting at the New Jersey Manufacturers Insurance Company facility on March 29, 2011, in Hammonton, New Jersey. 21 photos: Gay celebs and marriage Attorney Justin Mikita, left, and  Modern Family  star Jesse Tyler Ferguson announced their engagement in 2012 via their website tietheknot.org and then married in July 2013. 21 photos: Jenner's journey: From Bruce to Caitlyn Jenner attends the premiere of the reality show  Keeping Up with the Kardashians  in 2007 with, from left, Khloe Kardashian, Kim Kardashian, Kris Jenner, Kourtney Kardashian and Rob Kardashian. 22 photos: Photos: Attack on U.S. mission in Benghazi Attack on U.S. mission in Benghazi – U.S. President Barack Obama, with Secretary of State Hillary Clinton on September 12, makes a statement at the White House about Stevens' death. 22 photos: Sequel mania: A guide to the next 5 years of film When it comes to sequels, few have been as longed for as  Finding Dory,  the next installment following Disney/Pixar's heartwarming 2003 film,  Finding Nemo.  22-year-old college student blows her $90,000 college fund and blames her parentsYahoo Finance 3 All-New Luxury SUVs (2015)?AskThis.orgSponsored I don't like this ad? Thank you for your feedback We'll review and make changes needed. 23 photos: Photos: Malaysia Flight 17 victims remembered Malaysia Flight 17 victims remembered – John Paulissen, his wife Yuli Hastini and their two children, Martin Arjuna and Sri were all aboard the flight. 26 photos: Badass women of sci-fi Mal trusts no one more than his right-hand woman Zoe, played by Gina Torres, in the beloved, short-lived series  Firefly.  2. 8 How close do you sit in front of the TV when gaming? 29 photos: The most buzzworthy primetime shows of the fall  Supergirl,  premieres October 26, 8:30 p.m. ET, CBS: As the fall season starts, this late October entry perhaps represents the biggest gamble of all. 2. Amash personally explains every vote he casts on his Facebook page. ? 2. catch wind v. phr. 2. In 2009, under pressure from India the U.S. took the Kashmir issue off of the Kashmir-Af-Pak negotiations that was so vital to any peace plan and regional stability. 2. Stir the polenta flour into the onion and cook, stirring continuously, for two to three minutes. 2) The Language of Lust guide highlights the Importance of talking as well as how to talk to your man. 2. We're able to identify a conversation early enough that we have time to not only develop content, but get that content approved both internally and by our client and get it in front of legal with enough time to publish before the conversation ends. 2. You Try to Please Everyone One way to reduce the chances of being rejected is by trying to please everyone. 31 photos: Best images from Whistling Straits Round four – The tearful 27-year-old was congratulated by his longtime caddy and mentor Colin Swatton on the 18th green. 32 photos: Night of terror: Paris attacks Forensic police search for evidence inside the Comptoir Voltaire cafe after the attacks. 38 photos: World War II in pictures U.S. Marines of the 28th Regiment, 5th Division, raise the American flag atop Mount Suribachi, Iwo Jima, on February 23, 1945. 3 hours Politics Rep. Steve King, (R-IA) speaks during the Freedom Summit, Saturday, Jan. 24, 2015, in Des Moines, Iowa. 3.) Jesus was tough. 3. Kit-Kats: Kit-Kats could be a little lower down on this list, but when a Kit-Kat melts, it is ruined forever. 3rd DISTRICT: If anyone has information regarding any of the reported incidents, please call South Detective Division at 215-686-3013 or submit a tip. 3. The perils of email autofill The legislative committee on hydraulic fracturing delayed issuing its report until mid-January. 41 photos: The week in 40 photos A boy sits in a makeshift flotation device in a flooded house in Calumpit, Philippines, on Sunday, December 20. Flooding has hit parts of the country after Typhoon Melor struck. 42 photos: Two guys and their classic ride Shijiazhuang to Taian – Our BMW was a little thirsty. 42 photos: Two guys and their classic ride Shijiazhuang to Taian – We met many local people along the way. 44 photos: What a shot! 44 amazing sports photos Houston's Evan Gattis throws gummy bears into his mouth after hitting a home run in Oakland, California, on Wednesday, September 9. A teammate handed him the snack at home plate. 48 photos: Bill Clinton's life and career The Clintons, and their daughter Chelsea, center, depart the White House on August 18, 1998, with their dog Buddy on their way to a two-week vacation in Martha's Vineyard, Massachusetts. 4. Faith-inspired organizations help to spread the message of a healthy lifestyle -  which is the only affordable way to deal with non-communicable diseases of lifestyle, which are exploding in every nation around the world . 4. Its economic impact can be felt far and wide While it may not be able to quite match its footballing counterpart or the Olympics, the Rugby World Cup is still an incredibly lucrative business for its host. 50 photos: Hillary Clinton's career in the spotlight Andrew Cuomo, Eliot Spitzer and Clinton celebrate with a crowd of Democratic supporters after their wins in various races November 7, 2006, in New York. 51 photos: Syria's civil war in pictures Smoke rises over the streets after a mortar bomb from Syria landed in the Turkish border village of Akcakale on October 3, 2012. 52 photos: Barack Obama's presidency Obama adjusts an umbrella held by a Marine during a news conference with Turkish Prime Minister Recep Tayyip Erdogan in the Rose Garden of the White House in May 2013. 58 photos: All grown up: Child star transformations Jones was a chubby-cheeked youth in 2003 at the premiere of his movie  Bringing Down the House.  5. Help someone else. 5. If you think your child is troubled about something or may have a mental health issue, it's always worth seeking professional advice. 5. Skim off excess oil. 5. That post-credits scene Suddenly, we see Gillian Anderson's sometimes-accomplice Bedelia sitting down to dinner. 5. The risk of losing Greece to Russia Greece has been a member of NATO since 1952, but also has commercial, cultural and religious ties with Russia. 5 things to know about measles More from Opinion Opinion: Jordan's executions of jihadists could backfire ISIS has miscalculated Agony of defeat: For Seahawks, will it last forever? 5 ways virtual reality can transform business More from World Why we have Daylight Savings Time It's a $6.2 billion industry. 5. You care more about your success than their's. 6. CARIFORUM and the EU welcomed progress made in implementing the EPA, and in particular the duty-free quota-free market access for CARIFORUM exports to the EU, as well as the tariff cuts made by CARIFORUM States. 6 photos: Emmy nominations 2015 Outstanding lead actress in a comedy series – Laughs galore with Emmys' picks for the funniest women on TV. 7 Mistakes You're Making At Your Open House 15 American Ghost Towns Home values point to wide wealth gap within US cities Associated Press Time for baby boomers to take out reverse mortgages? 7 photos: 500 stormtroopers scale the Great Wall of China for 'Star Wars' trailer debut The event took place at the Juyong Pass -- or Juyongguan -- of the Great Wall of China. 7 photos: Tips from a caviar concierge Chill – Once you purchase your caviar, there's about a six-week window to enjoy it. 7. Singapore – The second most popular city in East Asia with visitors, Singapore is expected to see 11.88 million visitors in 2015. 7. We're all about Katy Perry today, apparently. 7) You'll learn to maintain a strategic distance from the sustenances that close down the fat smoldering machine. 8 of 9 What to see at Design Shanghai 2015 9 photos MRT Design – Shanghai's MRT Design has launched a line of furniture made of architectural materials, like this concrete table. 9/11: Then and now (58 photos) Yahoo News photographer Gordon Donovan recently returned to the scenes of many memorable images taken in New York City on Sept. 11, 2001.  95 percent, 99 percent, whatever the figure is, which will be of no particular relevance to the investigation,  Lewis said. 9 photos: Michael Stokes' photos of wounded warriors Redmond Ramos, a Navy corpsman, lost a leg after stepping on an IED. 9 photos: Osama bin Laden's compound Contractor Yusufzai looks at a bathtub left over from the demolition on May 1, 2012. 9 photos: Osama bin Laden's compound Children play cricket near the site of the demolished compound. 9-year-old girl critically injured after car crashes into LAX terminal A 9-year-old girl is in critical condition and two others were injured after a car crashed into the departures area in Terminal 7 at the Los Angeles International Airport Sunday. A 1950 article in The Ohio History Journal tells of multiple invasions of graves in potters' fields, where unclaimed bodies and poor people were buried. A 1995 study in the American Journal of Cardiology showed that appreciation and positive emotions are linked with changes in heart rate variability. A 35-year-old, non-national male from Turubah city developed symptoms on 28 June and was admitted to hospital on 1 July. A 44-year-old man who left for China on a business trip on Tuesday, a day after his father was diagnosed with the virus, was confirmed today to have been infected, it said. A 55-year-old female from Arar city developed symptoms on 6 March and was admitted to hospital on 20 March. A 65-year-old, healthy couple can expect to spend $266,600 over the course of their retirement on Medicare premiums alone, according to HealthView Services. A: Al-Qaida in the Arabian Peninsula, which Washington considers to be the group's most dangerous branch, has been thriving in the fallout of the Houthis' offensive across central Yemen, where Sunni tribesmen predominate. Abby Wambach announces retirementYahoo Sports Bindi Irwin's Risque 'Dancing With the Stars' Tango Leaves Derek Hough Feeling UneasyETonline Escape to Lanzarote this Autumn. Abdeslam's older brother has urged the suspect, who was last seen driving toward the Belgian border hours after the attacks, to turn himself over to authorities. Abdrabbuh Mansour Hadi - The president fled abroad in March as the rebels advanced on Aden, where he had taken refuge in February. Abdullah has outlived two other half-brothers who held the crown prince post. A beautiful, thoughtfully engineered chunk of heavy metal. Abeer Hallak of Mesquite attended the rally and said she was surprised by the strident nature of the demonstration. A  behind the scenes  YouTube video. A biochemical profile is created for each astronaut in the investigation.  A bit of bullying, a bit of sexual comments that you hear a lot and some women don't take it as well and some women are saying, 'I just need to have someone on the end of the line to call someone when i have a bad day,'  she says. A blow to confidence Most important, the decision would be a blow to confidence in government. Abortion Uruguay Film Click here to view Conversations Please check the checkbox to indicate your consent The Morning Email Get top stories and blog posts emailed to me each day. A botulism antitoxin arrived this week from the Centers for Disease Control and Prevention to treat those who are ill. About 100,000 of those jobs were in the private sector. About 10,000 of the luxury cars are fitted with the illegal software device, regulators said. About 10 percent of marijuana buds fail tests and can’t be sold in recreational pot stores, according to Liquor Control Board data. About 2200 guests are packed into the University Cultural Centre for the funeral, the State Funeral Organising Committee said. About 400,000 visitors come up each winter, while summer totals are closer to 10,000. About 40 percent of BES 12 wins in the first quarter were multiplatform. About 42,000 positions were added last month, much higher than the 10,000 predicted by analysts, as the jobless rate ticked down from an adjusted 6.1 percent in April. About 4.5 square miles had burned, Leingang said. About 70 hostages, including more than 20 members of the Iraqi Security Forces, were liberated in the helicopter assault, which involved U.S. special operations troops as well as Kurdish and Iraqi forces, U.S. officials said. About a handful of other referendum elections have been held on the reservation, including one that rejected a tribal takeover of federal health care services and three on tribal casinos. About a thousand were killed on the spot — including Nukic's father and brother. About five hours after Knowlton was shot, detectives went to First Presbyterian Church on South 248th Street, where Chancellor was known to hang out, and found the box truck parked next to a mobile home, the papers say. About Soldier Ride® Soldier Ride® began in 2004 when civilian Chris Carney cycled more than 5,000 miles coast-to-coast in support of WWP. “About two or three weeks ago I saw people making love in the grass a couple of metres from the play area. About Westbury Bancorp, Inc. Westbury Bancorp, Inc. is the holding company for Westbury Bank. Above all, the NSTU is terribly afraid of losing some of the generous benefits and contract language it has accumulated over decades. Abraham applied to have her case dismissed earlier this month saying the court process was a huge stress for her. A brilliant filmmaker, he brought quality & depth of character to every movie he made. A budding photographer, Verdi was on the lookout for a project and the club captured his imagination. Academic research wasn’t enough, though; the foundation wanted to get inside the clinics where women were making birth control choices and ensure they were aware of, and could afford, the most reliable options. A Cairo court last month banned militant soccer fan groups as terrorist organizations. A camera lens smaller than 3 mm detects a watcher's stare, and a computer algorithm maps exactly where they're looking. A campaign group said that 1.4 million people had signed a petition calling on the European Union to suspend glyphosate approval pending further assessment. A car can heat up 20 degrees in just 10 minutes, according to the National Highway Traffic Safety Administration, and a child's body temperature can rise up to five times faster than an adult's. A cashier notified police which dispatched an officer to investigate. According to AFP, Kiribati suffers from a range of environmental problems linked to climate change, including storm surges, flooding and water contamination. According to a report in 9to5 Google, the next Google Glass is an enterprise version. According to a report in the Times, this isn't enough for many leaders of the black community, who held a meeting last week on  What to do about Mayor Bill de Blasio.  According to Capital, the mayor has plans to close Prospect Park's West Drive to vehicular traffic, though the East Drive will remain open. According to Christian Bök, there are four ways to be a poet. According to City News Service, O'Farrell said that he grew up being taught that Columbus was a “great man.” According to City of Winnipeg officials, a maintenance backlog is causing a shortage of buses available for peak service times. According to court documents, the charges of distributing child pornography are related to incidents that allegedly occurred between last January and March. According to data from the OECD, Japanese women's participation in the labor force is around 63 percent, far lower than in other developed countries. According to Doug Schultz, a spokesman for the Minnesota Department of Health, Chipotle (CMG) has been responsive and cooperative during the investigation into contaminated produce. According to ESPN. According to Gomez, what if buildings not only reduced their emissions, but actually absorbed CO2? According to Jerry Burris, a political reporter and co-author of The Dream Begins, a book about how Hawaii shaped Obama, politicians in Hawaii are supposed to be mild-mannered and humble. According to Matthew Willsher, the Chief Executive Officer, Etisalat Nigeria, “Etisalat Prize for Literature serves as a platform for the discovery of new creative writing talents out of the African continent.” According to multiple recruiting services, Penn State was expected to have its entire 2015 signing class on campus along with four other undecided recruits. According to OpenSecrets, he has given more than $5 million in political donations since 2004. According to Outdoorsy, more than 12 percent of American households own an RV. According to Shah, Tahir Hussain Minhas, who masterminded the Ismaili killings, was a trained terrorist with expertise in bomb-making. According to Soshnick, the union sees such funding as a long-term investment that will generate even more stadium revenue, thereby expanding the salary cap and returning the benefit to players. According to the agreed statement of facts, “all of the accused’s descriptions of (her daughter’s) abilities to move and walk, as well as the amount they ate, is, as borne out by the medical evidence, patently false. According to the company, approximately one in three women in the United States has low sexual desire, and about one in 10 women feels distressed about it. According to the Ex,  Pearce allegedly had a stolen parking meter in his residence.  According to their children, Ron and Donna, Don had a son from a previous marriage named Dennis Wayne who passed away in 1986. According to the league's website, the program  focuses on providing talented New York City baseball players an affordable opportunity to improve their skills, extend their sportsmanship and compete at the highest levels of play.  According to the Ministry of Foreign Affairs, dozens of foreign delegations are scheduled to attend events this week in celebration of the ROC’s 104th birthday. According to the report, the IG did find, however, that  small payments were made to individuals in return for information relating to Sgt. Bergdahl's captors, location or physical condition. According to the Syrian Observatory for Human Rights monitoring group, people were drawing water from wells. According to this report “the volume of loans that marketplace lenders have extended has doubled every year since 2010 and hit $14 billion last year, according to Morgan Stanley analysts. According to Ukrainian intelligence, his staff is in Donetsk. According to VICE, at least 1,083 people in the U.S. have been killed by police in the last year. According to Wallace, Tiversa did this by using phony IP addresses -- on the orders of Tiversa's CEO, Bob Boback. According to Zhang, Xi had said that without the “ocean pacifying pillar” of that foundation, the cross-strait process would meet choppy waters during the course of its peaceful development. According to zoo officials, the power was restored Sunday evening, although repairs will need to continue over the next few days. A CCTV image shows the trio entering an elevator as they prepared to leave. Acheampong is a terrific player and I was confident putting him in. I wasn't worried about putting him in the team. A Chinese People's Liberation Army cadet sits in a Main Battle Tank during a demonstration at the PLA's Armoured Forces Engineering Academy Base, July 22, 2014. A collapsible metal ramp the state purchased for Abbott to ascend the dais in the House and Senate is among the few modifications made to accommodate a governor in a wheelchair. A commission official said the online polls are for reference purposes, since the final decision on the matter will be made by election authorities alone. A commonly used one is the Standard and Poor’s 500 stock index, which is the value of 500 stocks that is tracked every day on the financial markets. A couple celebrates at San Francisco City Hall upon hearing about the U.S. Supreme Court rulings on same-sex marriage in June 2013. A couple of meters away was Tim Mälzer, Germany's answer to Jamie Oliver. A craze for roller skating has hit Kenya, fueled by its growing middle class and a love for speed. Across all 16 specialties, only 144 U.S. hospitals performed well enough to be nationally ranked in one or more specialties. Acting as Queen Bee of the group is their teacher, Christian MacKinnon, who runs the Botwood Mountain Bike Club. Action film star Maggie Q and orthopedic surgeon-turned-shoe mogul Taryn Rose are also Vietnamese-American. Actor Meshach Taylor 67, died at his home near Los Angeles. Actress, Drama Series: Viola Davis,  How to Get Away With Murder,  ABC. Actually it's not surprising that he expects people to believe his story. A Cultural Heritage Management Plan has been produced to guide activities related to archaeology. A date for that hearing has not yet been set. A day after organizers for the Austin-based media festival announced that it had canceled the panels after receiving threats of violence, BuzzFeed and Vox Media both said that they plan to withdraw from the annual event. A day earlier Osborne agreed with top Chinese officials in Beijing to study the linking of trading on the London and Shanghai bourses, and the possibility of China's central bank issuing short-term bonds denominated in the yuan currency in London. Ad Choices Link your subscription Already have a login? Additionally, he remarked that a wide gap exists in respect of succession planning. Additionally, she is a Senior Policy Advisor and expert on health economic analyses mainly focusing on Medicare, Medicaid and health reform, specifically as they impact vulnerable populations. Additionally, she wants to see more culturally appropriate protocols and procedures put in place. Additionally, the Charging Unit is staffed around the clock in The Widener Building. Add that to spacious suites with floor-to-ceiling windows and a beautiful spa and you're officially ready to treat yourself. Add the crisis in Ukraine that has plunged relations with Russia to their lowest point since the Cold War, and the looming threat of a possible British exit from the EU in a referendum due by 2017, and the problems mount.  A decision in favour of Heathrow expansion is really just a decision in favour of delay and fudge,  he added. Aden (AFP) - An overnight drone strike killed three suspected Al-Qaeda militants in Yemen, a country where the United States is the only power operating the unmanned aircraft, a tribal source said Monday. A DeRozan dunk cut the lead to 106-104 with 15 seconds remaining, but Westbrook made a pair of free throws and DeRozan missed a shot from three point range to seal Toronto's fate. A discussion group that is strictly focused on sexual topics and relationship issues related to being a Gay male. 322-2437; www.sasgcc.org Debauchery. Adjusted earnings totaled 54 cents per share. Adopting these two strategies will help you be better able to reframe stressful events. A drop of around 20 million by 2060, according to estimates from the German government statistics agency, Destatis. Adult entry costs £6 and child entry is £3. Aduriz has only one cap to his credit for Spain, which he earned as a substitute for the final 15 minutes of a 3-1 win over Lithuania in 2010. Advertised as an  alternative  to marijuana, synthetic marijuana is a class of designer drugs made up of psychoactive chemicals that are sprayed onto plant material and then smoked or eaten to produce a high. Advice: if you still haven't fully prepared for the night on the town when you are considering reaching for the Uber app, do everyone a favor and wait until you're photo-ready before you tap your phone for your ride. ADVISORY COUNCIL: Members of the Berggruen Institute's 21st Century Council and Council for the Future of Europe serve as the Advisory Council -- as well as regular contributors -- to the site. A DVLA spokesman said:  We are very sorry for the inconvenience caused to Mrs Southfield who did exactly the right thing in taxing the vehicle as soon as she bought it. A: Efinaconazole (Jublia) is a new topical antifungal medication that is being widely advertised on television. A European Commission spokesman said Friday that fighting the smuggling of migrants would  continue to be a priority  of the EU in 2015. A European source said the  first priority is to reach a deal on what happens after February 28 , the date to which Greece's bailout was extended by its creditors at the end of last year. A&E wants 20 more episodes of 'Bates Motel' 1 day ago LOS ANGELES, June 25 (UPI) -- A&E Network says it has ordered 20 more episodes of the acclaimed drama series  Bates Motel,  which stars Vera Farmiga and Freddie Highmore. A federal judge issued a preliminary injunction in February against DAPA and the expansion of DACA, and the U.S. Court of Appeals for the 5th Circuit is currently considering whether to allow that injunction to stand. A federal judge says a former bartender charged with threatening to kill House Speaker John Boehner is competent to stand trial. A few hours later, about 5,000 people gathered at a square in front of parliament, in response to a call on social media for a show of solidarity with the government. A few hours later, Aurora officers responding to a call were shot at. A few minor characters like Miss Moneypenny, M and Q, have been part of the picture for years and have long been cult. A few months from now, SpaceX says it will put the Crew Dragon through another in-flight abort test from Vandenberg Air Force Base in California. A few snowboarders, including Graham Merwin, took advantage of this late-season snowfall and hiked up the high mountains. A few times, the children were taken to see her at a McDonald’s, but then those visits stopped. A few weeks ago, they shared a friendly sushi dinner with some relatives in Malibu, California. Afghan forces struggle to retake Kunduz from the Taliban Afghan forces struggle to retake Kunduz from the Taliban 01:44  We can't rely on foreign forces. Afghanistan's Interior Ministry spokesman Sediq Sediqqi said Wednesday night that the Kandahar attack was over. A final blow came earlier this week after the New York Stock Exchange moved to delist the company's battered shares. A fine speaker, I saw him as a clearly ambitious and calculating politician. A fire might only render your family homeless, as opposed to a storm that renders the whole neighborhood homeless. A Fire Weather Watch means that critical fire weather conditions are forecast to occur and to watch for Red Flag Warnings. A first-degree murder conviction comes with an automatic life sentence with no chance of parole for at least 25 years. A flat market implies a decline of 40% in overall earnings per capita (real dollars) which isn't very likely. A Florida Atlantic spokeswoman said the school would not comment beyond its Dec. 16 statement that announced the start of termination proceedings. A formal decision to renew the EU sanctions on Russia's financial, energy and defense sectors is expected to be taken when the leaders next meet on June 25 and 26. African gray crowned crane An African gray crowned crane (Balearia regulorum) -an endangered species- and her two-month old baby are seen at the Santa Fe zoo in Medellin, Colombia, Feb. 10, 2015. A frigate at Russia's Baltic Sea naval base at Baltiysk in its Kaliningrad enclave was the venue chosen by Rogozin and President Vladimir Putin on Sunday to underscore the doctrine's release. After about 6 miles, runners follow a loop back to the finish line on the Virginia Beach Boardwalk.  After a less-than-stellar solo career, Brown and his former bandmates enjoyed a revival when You Sexy Thing featured in The Full Monty.  After all, the former mayor and his defense team followed the book. After an age when all photos were said to lie, we seem to have returned, in this glum digital era, to an unquestioning faith in photographs: take a picture, or it didn’t happen. After another defensive stop, Durant put the game away when he took a pass from Westbrook and threw down a dunk baseline to give Oklahoma City an 11-point cushion with 1:40 to go. After another insult, (but this time specific to Rosie O'Donnell), he admitted his insults were not just about her, saying he was  sure  it was well beyond Rosie O'Donnell. After a weeklong review of the situation, experts from the World Health Organization and South Korea on Saturday downplayed the possibility of the country's MERS outbreak turning into a pandemic. After been given consent from Long to search his house, Officer Grinnell found a small baggie of methamphetamine. After big events in Berkeley, such as football games at the University of California, hundreds of cars would back up for hours waiting to board the ferry for the trip back to San Francisco. After challenging the actress to a race around Universal Studios, Conan fell and bumped his head, giving himself a concussion. After day you can get the healthiest option unity to get salad you can get a quite that's where thing. After dozens of tests, they found DNA from Hymenolepis nana, the dwarf tapeworm, in the man’s tumor in mid-2013. After eliminating users who didn’t make their tweets public, who hadn’t tweeted recently or who didn’t tweet very often, they were able to analyze 466,386 tweets by 5,386 Republicans and 457,372 tweets by 5,373 Democrats. After filling, tape the top of the bottle closed with duct tape or tape that will not tear. After George's initial report, much of the IRS's top leadership was forced to retire or resign, including Lerner. After graduating, Dow moved to New York City to work with the likes of dance greats Hanya Holm and Martha Graham and acting mentors like Michael Shurtleff and Uta Hagen. After her firing, Twitty told the AP that she shared the messages as funny items she didn't consider offensive. After her surgery, she was resigned to having chemotherapy, she said, and didn't feel she could afford the test.  After June 30, we don't have an agreed deadline,  a senior Iranian diplomat told CNN on Monday. After noting that there is no  mechanism  for revoking the honor from Cosby and after noting that he tends  to make it a policy not to comment on the specifics of an ongoing case,  Obama made a point about the Cosby case and rape. After reading Charmaine's nomination, judges from kmfm and Kent Reliance decided they should be named Kent 999 Emergency Person of the Year in the Kent Hero Awards. After regaining control of its Facebook page, the network posted a brief message saying that:  This Wednesday, our network, our websites, antennas and social media were 'hacked by an Islamist group'  and that teams were working to restore service. After rejecting the idea, judges set July 7 as the date for opening statements to begin in The Hague. After Samsung kicked off the wireless charging revolution with the Galaxy S6 this year, many expected Google would have to follow suit with its new Nexus phones. After several “pardon mes” and “coming throughs,” we got the tripod inside and forgot about the looming storm. After six months of lessons, Faraj joined the New York dancers for intense practice sessions in Amman — preparing for his solo and for a performance with his two American mentors, Sean Scantlebury and Mira Cook. After six years as an illegal immigrant in London, he was deported home to Afghanistan, he said. “After speaking with the police and my lawyer, I have pressed charges against Aisleyne and am considering issuing a lawsuit against Janice. After starting in algaculture in 2013, Mondal says she is now able to save money for a rainy day. After stints as a professional wrestler and worker at a N.W.T. diamond mine, Tricoteaux moved to Vancouver where he initially worked as a stuntman before landing his big break. After sweeping south towards Baghdad, the militants again turned their attention to the north in August, driving Kurdish forces back toward their regional capital and seizing areas including Sinuni. After taking office, President Clinton chose his wife to head a special commission on health care reform, the most significant public policy initiative of his first year in office. After that, he found it tough to sustain drives against Denver's defense, which is ranked No. 1 against both the run and the pass and held McCarron to 200 yards passing and the Bengals to 3.3 yards a carry.  After that,  Liu says,  I shifted my focus to heated social issues.  After the cash piles are sorted the family of three is left with only 10,000 roubles (£107; $167). After the Cavs swept the Celtics, they advanced without Love and not missed a beat. After the massive explosion, which scientists believe rivaled a 100-megaton nuclear bomb, the volcanic debris caused vibrant red sunsets and the moon to have a bluish tint. After the official opening of the clinic, physicians, medical students and medical residents gathered at Canada's most famous port of entry for immigrants — Pier 21 — to protest the cuts. After the Paris attacks: — The Omaha Islamic Center in Nebraska reported that someone spray-painted a rough outline of the Eiffel Tower on an outside wall. After Tuesday a ridge of high pressure over New England should bring a stretch of fine weather and above normal temperatures for the remainder of the week. After voting at a school attached to a military camp, the president called for people to patiently wait for results. Afterwards former Prime Minister Tomiichi Murayama complained that Abe's statement did not clearly reflect or embrace the spirit of his historic 1995 statement that serves as the gold standard of Japanese mea culpa. After years of bullying Angie, Den Watts finally met his match in Chrissie, who gave as good as she got. A G8 with Russia though is an idea whose time has long passed. Against the Lady Bobcats, the offense wasn't the problem, it was the defense. Again the type of game they come out, get two or three chances and allows us to settle into the game and grab that first goal,  Wild coach Mike Yeo. Agents seized nearly 300 rifle parts and several computers. A German daily reports that Berlin plans not to accord full refugee status to those fleeing Syria's civil war. Aggressive copyright laws like the DMCA compound that advantage, giving grounds to prosecute anyone who breaks through the car company's digital protections. A good seller will build on that as opposed to repeating it. A good show boys. A government report issued Wednesday showed that sales of new homes were basically flat in January, evidence that the relatively low mortgage rates and recent job gains have yet to spur the real estate market. A graduate of Yale and the University of Chicago Law School, she served as the Hennepin County attorney from 1999 to 2007. A group of international affairs students launched GW’s chapter of the No Lost Generation campaign last month. A growing number of paying customers are now picked up far from Calais, sometimes  around Paris, but also Belgium or the Netherlands,  said Arassus. A gulf opens up between the real deaths it draws upon and its ability to fully represent them. A half-inch adjustment in his putting grip has paid off at Colonial, with only 25 putts in each of the first two rounds. A Harborview spokesperson did not immediately return a call for information on the boy's condition.  A hate crime is one of the most vile types of crime, because it is an attack upon identity and community,  Margaret Parsons, executive director of the African-Canadian Legal Clinic, said in a news release ahead of the press conference. A healthy diet, like the Mediterranean diet, is a good way to go. A helicopter and officers were spotted searching the grounds of the hospital shortly after midday. A higher snowpack translates to more water for California reservoirs to meet demand in summer and fall. A high point of “Submission” is François’s miserable retreat at Ligugé Abbey, where Huysmans took his vows. Ahmet Davutoglu said evidence point to  a certain group  which refused to identify in a group interview on Monday. A host of political luminaries attended. Aides on both sides have pointed out that when the two leaders met in Washington in September, there was a palpable chemistry. Aid workers from the group are on the ground, preparing to hand out food and water. AIG has since returned to financial health and fully repaid the bailout. Airbnb.com has a large listing of casas to choose from. Airbnb has been recruiting hosts in Cuba for three months and plans to expand into other cities in the coming months. Air India grounds 'heavy' cabin crew More from Karla Cripps Journeys with germs: What are the dirtiest things on an airplane?  Air shows should be over the sea,  she said. A) is a television broadcast company headquartered in Atlanta, Georgia, that owns and operates television stations and leading digital assets in markets throughout the United States. A: It was a challenging year for us for sure. A judge said as a business, Ashers was not exempt from discrimination law. A jury on Friday found them not guilty on the involuntary manslaughter charges, but Kenny was convicted of cruelty to an inmate. ALANNA DURKIN View Comments No personal attacks or insults, no hate speech, no profanity. A large protest that started at the Mal … The protesters left the mall Wednesday afternoon and were walking toward a light-rail train station outside the massive suburban Minneapolis mall. Alaska said that “in exceptional cases” if a hoverboard is somehow required as a mobility aid, then it could be allowed on board. A lawsuit has been filed against the museum. A. Law': Where are they now? 24 photos Susan Ruttan played Arnie Becker's long-suffering secretary, Roxanne Melman, on  L. Alberta Health Minister Stephen Mandel has said what the college is doing infringes on paramedics' rights. Alberta's NDP government announced the formation of the panel on Friday. Alcocer said the first surgery already has impacted his life in a powerful way. ” “Alderman Arena asked me to pass on to you an answer to your question regarding repealing the rule that says food trucks must be 200 feet away from a restaurant in order to operate. A lengthy fight could hurt Poland's image as a model of post-communist transition. Alesha told her she fell in love with her before she even started, and Simon said he felt like he was being “welcomed into heaven”. Alexander, whose parents are Christians, attributes his unique talent as being  a gift from God.  Alexion shares were off more than 10 percent at $151.65, signaling investor dissatisfaction with the price the company agreed to pay for Synageva. Alex Sullivan was killed while celebrating his 27th birthday and wedding anniversary in the July 2012 attack. Alfred, Lord Tennyson Editor’s Note: This is the second of three columns on New Year’s resolutions. Alibaba has been seeking to strengthen its electronics offerings in recent years, inking tie-ups with Gome Electrical Appliances Holding Ltd and Haier Electronics Group Co Ltd to offer home appliances online.  A lift in average transaction prices can be attributed to consumers' continued interest in buying trucks and SUVs,  said Alec Gutierrez, senior analyst for Kelley Blue Book. Alisha Valavanis, the Storm’s president and general manager, said numerous candidates were considered, but Boucek was a front-runner from the start of the search process. A list of 45 people, including colleagues and passengers who had sat close to him on the Hong Kong-bound flight, has been drawn up. A little over a year ago, Cramer warned that sand plays were in danger. Alki David, who is the CEO of Hologram USA, also founded FilmOn. All 10 individuals on the Wealth-X Hollywood Rich List are male film producers and directors. All 64 students, staff and sailing crew on board escaped safely, spending almost two days in a life raft before being picked up. All ‘Ale the Ladies Happy Hour at The Black Squirrel, 5 to 7 p.m. Celebrate the women of craft beer with this meet-and-greet happy hour featuring 12 women in the craft beer industry. All around me, tree limbs shake. All construction contracts went to Greek companies, he says. All contributions will go directly to the organization's partner hospitals. All Curry did since then was win the 3-Point Contest at All-Star Weekend, capture the NBA's Most Valuable Player award and help lead the Warriors to its first championship in 40 years.  All forces are on alert,  Tsipras said, noting that the hot, dry and windy conditions made firefighting more difficult. All in all, Taiwan has very good healthcare. All items will be sold or otherwise disposed of. All it takes is an annoyed neighbor's complaint and the Code Enforcement will be knocking on the door to the landlord's rental property. All offered a warm welcome for the new business, an operation that has resulted in six new jobs for the local community. “All of our stuff, including paper, has emotional attachment for people, and they come up with the same sort of excuses for why they need to keep it,” she says. All of that makes sense to me. All of these abuses must be stopped,  he said. All of these goals can require serious cash -- and now is the time to start getting it together. All of this has lead me as a professional social worker to reflect on why it so difficult for us as a society to hear the lived truth of victims of sexual violence?  All of those things, I'd say, are enormous victories or huge steps, and those came about because of Brittany's story,  Diaz said. All our stories are at bbc. Allowing it would give  women who carry these mutations greater reproductive choice,  he said. All personnel at the refinery have been accounted for, and officials say the fire is contained, though it continues to burn as Allen County firefighters attempt to douse the flames. All seven Marines involved in the crash were based out of Camp Lejuene, an expansive North Carolina base that is home to seven major Marine commands, one Navy command and a total of about 170,000 active deputy, dependent, retired and civilian personnel. All six pleaded not guilty. Allstar said it has already made changes to its business to make costs easier to understand. All the evidence shows that a healthy meal helps with that.  All the evidence suggests he acted in self-defence,  Corey said. All The Way by TALsounds Contact the author of this article or email tips@chicagoist.com with further questions, comments or tips. All those qualities added up to a coach that was able to get the best out of so many football players, and earn the respect and admiration of players and fans alike. All told, the saga spanned four nights and took some eight hours or so. All told, this was a terrific, exciting, tense episode. All were cleared and released by authorities. Almost all of the new arrivals came to the Austrian town of Nickelsdorf, on the border with Hungary, before boarding buses and trains that took them to emergency accommodations elsewhere. Along a windy stretch of Old Belfair Highway, at an obscure curve in the road, Bremerton Police Officer JD Miller spotted something along the shoulder that just didn't look right. Along the route east from California, drivers would slow down to read his signs on his rickshaw and then come back with food they prepared for him at home. Along with her fellow Emmy®-nominated choreographers, Holker prepared a piece that was performed at this year's Primetime Emmy Awards with Neil Patrick Harris. A look at his Preakness victories: The Preakness seems like a bonus for Baffert after coming up with a Derby win two weeks earlier to ease the disappointment of a photo-finish loss to Grindstone in ‘96. A look at the distribution of seats in the present parliament makes this abundantly clear.  A lot of knives will have a little spring assist so when you push it open with your thumb, the knife will open up pretty much by itself.  A lot of people are disputing that climate change is a reality because they don't see everybody going under water.  A lot of people would agree with stop-and-frisk if it’s for the safety among us. A lot of that is on me, throwing picks and stuff like that, fumbling, things we haven’t done that much this year.”  A lot of the problem is that people get in their respective corners. A lot of things that were once hand painted are now being replaced by stickers,  explained Suman. A love of certain musicians or bands seems to drive many impulsive tattoo requests. Al-Qaeda in the Islamic Maghreb and other jihadist groups also carry out operations in the area, including the 2013 murders of French journalists Ghislaine Dupont and Claude Verlon. • Al-Rishawi is an Iraqi prisoner on death row in Jordan for her role in 2005 bombings. Also demand that the Berkeley City Council adopt a paid sick leave ordinance. Also evident from the analysis, experts say, is that people across the country are eating less fruit.  Also I'm male, so I'll never get pregnant,  he joked. Also killed when their mobile home in Ripley was swept away late Saturday were two of Kennard's children, 7-year-old Gabriel Barrios and 5-year-old Rosa Barrios. Also on Monday, German prosecutors said they were looking into, but not yet formally investigating, claims that Germany bribed football officials to win the right to host the 2006 World Cup. Also, since ocean waters are unusually warm, that will also prevent the temps from cooling down very much in the evenings. Also Wednesday, the Border Patrol said 10 undocumented immigrants were hidden behind a fake wall inside a commercial truck at a checkpoint in Falfurrias, about 90 miles north of the border. Alternatively, operators could use the approach to purify their transmissions by removing extra photons, Dayan said. Although aimed at an older age group than the six-to-14 year olds being targeted in UAE, the Italian approach nonetheless mimics the operating model and public-private partnership established in Ras al-Khaimah. Although companies have slashed spending and taken numerous rigs offline in the U.S. in recent weeks, he stated, over time declining production overseas will require U.S. drillers to step supply back up. Although saying,  The wicked Japanese imperialists committed such unpardonable crimes as depriving Korea of even its standard time,  may be a bit over the top. Although the crashed vehicles were cleared quickly, long tailbacks built up due to the heavy rush hour traffic. Although the news comes as a huge blow for the town, it was not entirely unexpected - as predicted on KentOnline in October. Although the population of the region is approximately 40% to 50% ethnic Kurds, it also includes large numbers of Sunni Arabs and Turkoman. Although there are no official figures for how many women use such products across Africa, billboards advertising them can be seen across the continent. Although there is a cap in California on the number of homes that can receive net metering credits, the Legislature has consistently raised it as the industry has grown. Although the two are grown adults now, Bridgewater said awareness is important, especially considering the increase in diabetes diagnoses she has witnessed as a school secretary.  Although they are outside of East Turkestan,  he said, 'They are in the same fear that they were in East Turkestan.  Although they have allocated billions of dollars toward public schools, critics say that's not enough to meet the requirement in the state Constitution that education be the Legislature's  paramount duty.  Although venomous, Pauly said yellow-bellied sea snakes are generally harmless when left alone. Although you may not fully recoup your kitchen or bath remodel, there are other factors to consider. Altogether this is one of the sexier examples of globalized innovation — if you’re turned on by exotic materials, advanced manufacturing and fire-breathing supercars. A 'major' temple and a film star's former home Closeup of the forest This is no coincidence. A Manhattan at Harlowe (via Facebook) Harlowe could easily be mistaken as another decades-old institution, but this classy West Hollywood outfit is just over a year old. A man's body was found inside the truck. A man walks past the London Stock Exchange in the City of London October 11, 2013. A man who was known by no one is now known by everyone. A Map Maker account with the handle nitricboy looks to be the responsible party. A Marine Police search boat has has gone out search the area daily since the disappearance. A masked man yesterday attacked a school in southern Sweden before being shot by police. A masked suspect came out from behind the building. Amassed over decades by a private collector, the letters represent one of the largest caches of Einstein's personal writings ever offered for sale. Amateur astronomers will have a shot at seeing the asteroid, too. Amazon lifts the veil on Prime CNBC 6 hrs ago Leslie Shaffer Amazon (AMZN) typically plays its customer data close to the chest, but on Monday, the e-commerce giant let a few figures about its Prime service trickle out. A meeting is to be held in Igloolik prior to the video conferencing to prepare the victims, and a mental-health worker is to travel to Iqaluit with those who will deliver impact statements in person. A meeting of OPEC technical experts in Vienna on Oct. 21 may give indications whether sentiment is shifting within the organization about maintaining production levels as prices remain muted. American Muslims also continued to serve as punching bags for the GOP field, with some candidates repeating calls for increased surveillance of mosques and more aggressive monitoring of other activities that could be seen as  radicalizing.  American Photographer Stephen Wilkes will be talking about how he captures the transition of time in his photographs, Thursday at p.m. at The National Gallery of Canada. American regulators have essentially refused to push back against corporate bee killers. Americans are being shot and killed in places we should be able go every day without fear: work, school, church and the movies. Americans are much more likely to avoid soda now than they were over a decade ago, in 2002, when 41 percent said they avoided the beverage, according to Gallup.  Americans have a love-hate relationship with debt. Americans in the AP-GfK poll also said spending cuts would be worth a government shutdown by 56 percent to 40 percent.  American's maintenance program for the 787 will ensure that the aircraft will be completely powered down every 120 days until a software upgrade is developed and released by Boeing,  she said. American star Allyson Felix looked to have dug the Olympic champions out of an early hole with a blistering third leg to give McCorory a lead into the last lap, until Williams-Mills kicked coming into the home straight to win it for Jamaica. Americans were already nervous about terrorism after Islamic State militants killed 130 people in Paris in Nov. 13 attacks. America's CBS News quoted an official in Dominica as saying the death toll had reached 20, with several people still missing after mudslides destroyed dozens of homes. America's sophistication is reflected in the depth of its financial markets.  A message left with Murphy's attorney was not immediately returned. “AMIDuOS can run nearly all of the Android applications available in Android app markets,” the company says on its website.  Am I gonna check every statistic?  the Republican presidential front-runner told Bill O'Reilly on Monday night's  The O'Reilly Factor  on Fox News. Amini was responsible for twenty-eight pages. Amirali Pour Deihimi, an Australian architect who works on sustainable design, says the rule could keep him from attending conferences and seminars in the US. A mix of large and small non-venomous snakes is used to deliver a range of deep and light massages. Amnesty International claims that Nigeria's military is responsible for the deaths of 8,000 detainees. Among blacks, 71 percent thought police were more likely to use deadly force against black people in their communities, and 85 percent said the same thing applied generally across the country. Among his guests was Ray Charles, whose version of  Night Time Is the Right Time  would play such a standout role on  The Cosby Show  years later. Among Republicans and Democrats in Congress, Cuban-Americans such as Sens. Among the 31 plaintiffs in the cases that will be argued at the court on April 28 are parents who have spent years seeking formal recognition on their children's birth certificates or adoption papers. Among the assistant captains, Nash is a former Blue Jackets captain who will be returning to Columbus. Among the key steps are to change passwords, notify key stakeholders, access or make backups of data, and contact your insurance firm if you have cyberinsurance. Among the many notable series were  Columbo  (1971-78),  Kojak  (starring Telly Savalas, pictured, from 1973-78),  The Rockford Files  (1974-80),  Police Woman  (1974-78),  Baretta  (1975-78) and  Starsky and Hutch  (1975-79). Among them will be a first-person shooter — the genre that's proven difficult for developers to master in the VR realm. Among the other initiatives: —expanded internships and fellowships. Among the outlets the FCC monitors are nearly 9,000 radio and television broadcasting establishments, over 5,300 cable television systems, and nearly 12,000 wireless telecommunications carriers, along with satellite and wired communication services. Among the six insurers, CCB Life Insurance Co (????) and Cathy Lujiazui Life Insurance Co (???????) began reporting profits last year, while others saw steady growth in their China operations, the commission said. A month later, I'm less worried about terrorists with a medieval worldview than I am about politicians of Western democracies. Amtrak says there were no life-threatening injuries reported. 12 p.m. Authorities say an Amtrak train headed from Vermont to Washington, D.C., has derailed in central Vermont. Amy Berg, whose  Deliver Us from Evil  (2006) was nominated for an Oscar, premiered her latest film  Prophet's Prey  at the Sundance Film Festival this week. A mythical University in Canada where many good Engineers and Computer Scientists come from. An added bonus is that your phone will charge while connected to the controller. An adult not affiliated with the school was also arrested for selling drugs on school property. Analysts don’t believe India will be subject to the same slowdown as its Asian neighbor, regardless of GDP methodology alterations. Analysts speculated that the channel's suspension might ease the way for Egypt to free the two foreign journalists in line with a recently-approved law that allows foreign citizens to be deported rather than jailed. Ana made landfall at 6 a.m. EDT Sunday as a tropical storm along the coast of South Carolina, almost midway between Myrtle Beach and north Myrtle Beach. An amazing new video takes viewers along for the ride during a NASA spacecraft's epic July flyby of Pluto. An amazing pass takes @Lemanracing from 4th to 2nd and a place on the podium. An American, she served as economic adviser to Alex Salmond MP at the Westminster Parliament in London and as special adviser to him as First Minister in the Scottish Government. An Anglo-Italian firm called AgustaWestland is test flying two prototypes of a VTOL airplane called the AW609 TiltRotor. An Associated Press investigation found 268 perimeter breaches since 2004 at airports that together handle three-quarters of U.S. commercial passenger traffic. A native of Quitman, Ga., Mr. Cowart was the son of the late Joseph Alton Cowart and Lillie Hunter Cowart Lowe. An attack on Grabo in February 2014 left at least four people dead, including three civilians. An attorney for Omar Gonzalez said in court Tuesday that lawyers are close to resolving the case with a plea. And 30 seconds later, not even 30 seconds, we hear,  we're coming to rescue you!  And, again, any ship has specific terms of operation, it is about 20-25 years. And all kids, health issues or not, should follow the same basic safety guidelines on Halloween night, said Dawne Gardner, injury prevention coordinator at Cincinnati Children's Hospital Medical Center. And all of it was done to mitigate a moral or physical hazard. And all this had a corrosive effect. And Annie Lennox was a powerhouse when she sang  I Put A Spell On You  and joined Hozier for  Take Me to Church,  which was nominated for song of the year. And a record 2.1 inches fell at Quillayute Airport on the coast, breaking the old record for the date of 1.35 inches set in 1999. And around that time I was really getting into the hobbyist electronic scene working with this microcontroller called the Arduino. And arrest warrants are the only recourse cities have to get people to address their tickets. And, as Buzz Bishop, a father of two boys, ages 5 and 7, and founder of the blog Dad Camp, found out, kids, even as young as his sons, pick up some  birds and bees  talk on the playground. And as footballers get paid very well due to their fan base, surely that should be the norm to be nice to the fans or wallets. And as of March 2015, 99 countries have abolished the death penalty for all crimes, says Amnesty. And back in 2006 he was for a comprehensive immigration reform bill that had a path to citizenship. And because a key priority for older adults is ensuring the prosperity of their family, making sure the world remains a stable environment could fit easily into the narrative of caring for those they prepare to leave behind. And because they have a subject element of interpretation, they are not totally objective. Anderson Cooper: Better than anything. Anderson Cooper: It can turn bad very quickly? And Evan is right — it is kind of annoying to hold your thumb down. And even the many employers that do their best to abide by them can end up violating them inadvertently. And even voted for an assault-weapons ban.  And, finally, the story he told about the Ritz-Carlton gangs. And Germany is in a state of emergency. And he hoped that the British government was listening, saying the issue of economic mobility was not the same as the far more toxic issue of immigration - at least as far as Indian businesses are concerned. And he is not the only one.  And here I am now in this great place,  said Bruusgaard. And he’s doing all those things to become a better player.  And how quickly they get processed and sent to their secondary destinations like Edmonton Calgary and so on, we're not sure yet.  And how's that growth working? And how strong are they? And I already know what he looks like with gray hair, so no unpleasant surprises down the road.. And I don't like any free time,  she said. And if anyone figures out what the deal was with that kickoff in OT, please tell the rest of us. And I felt so torn, how do you - GALLOWAY: It's so difficult, because you've got great, great writers - Celine - you've got a Leni Riefenstahl.  And if I get the message the next morning, if I'm lucky, I'm probably too late because they booked somewhere else.  And if nothing else, it enabled the 22-year-old Texan to cut even deeper into McIlroy's lead. And if they keep paying, landlords will keep creating these hostels, at least until the bubble bursts and all the transient people leave. And if we don't guide that change democratically and in dialogue, it will simply happen upon us, and it will be an awful change for the worse. And if you miss the live show, you can always watch the replay (using the embed above) or download the audio version on iTunes. And if you’re looking to tour some natural surroundings, feel free to explore the island’s three national parks, two of which are home to a large number of birds. And I hope you'll join me on this journey.  21 photos: Who's running for President? And I love it at the restaurant.  And I'm proud of doing it.  And in an excerpt from the interview, Davis tearfully said those names don't define her. And in a sign of growing rivalry between the groups, the Taliban on Wednesday condemned a  horrific  video that appears to show IS fighters blowing up bound and blindfolded Afghan prisoners with explosives. And in some ways, that comment is aimed at the audience for  Supergirl,  which premieres Monday night after months of hype (including a brief mention that made headlines in the presidential campaign). And in the latest instalment of tenuous day-naming, today is supposedly Red Tuesday, the top day of the year for dumping your partner. And I pick her up.' And I remember a lot of pain,  she told  Entertainment Tonight.  “And I remind myself that you did not set out that day to kill anyone but what you did was a brief but dangerous manoeuvre.” And it has an appealing swagger to it: when Kendrick raps  Life ain't shit but a fat vagina,  you hear echoes of his earlier  Backseat Freestyle,  with its giddy promise to fuck the world. And I think, certainly, it's been also a great learning experience for everyone involved,  said Rhoades. And I think LauncherOne will be positioned to capture a lot of that market.   And I think what we need to do is to run against her because she has a failed foreign policy and because she doesn't have a record of accomplishment.   And it is sad, because it is preventable,  said Lisa Rogozinsky, the co-ordinator of the Edmonton Fetal Alcohol Network Society. And it is there that we talked about her writing, including her published 1995 collection of poems and short stories, The Dusk of Dawn. And it just so happened that Leica lent me its new camera to test the very weekend the Greenwich Concours d'Elegance was happening. And it killed  only  20 million people, less than half of World War II's 50 million.  And it's all about exposure, of course, to get people to know more about our company and what we do and what we stand for.  And it's extremely different from planets such as Earth, which have been transformed so dramatically by multiple collision, volcanoes and other events that it's almost impossible to decipher what they looked like when they were born. And it wasn't even close. And I've had great staff here. And, I was also fortunate to hear Indra Nooyi speak and she was brilliant - her realistic view on trying to be a good wife, mother and business woman really struck a chord with me. And Krugman agreed with some other economists who have said a Grexit shouldn't be underestimated. And Lego was able to tell me how many Stormtrooper mini-figures it has sold. And met Harry DeCosta, who was a concertmaster for the New York Philharmonic then, and we did a lot of jazz things, and I learned how to transcribe Herbie Hancock solos and Toots Thielemans’ solos and put it down for violin. And Missouri law enforcement was criticized for reacting with heavy-handed military tactics. And, most school meal programs are not available during the summer months, leaving millions of children to find an alternate source of nutrition. And my co-pay was 5 bucks,  she said. And my inside voice is—is me. And Nebraska's Norman Regional Hospital went on generator power after suffering minor damage. And now my face is right there, smack in the middle of those windows. And others offer short-term deals on their websites. And perhaps just as important, his delivery ran counter to the pre-event image that he lacks charisma. And perhaps the country of human rights suffered from an excess of human rights: under the pretense of free speech, how did so much anti-France discourse take place, and how did so many dangerous mosques stay open? And please, parents, don't you dare borrow a penny if it puts your retirement security at risk. And Ratliff's impact on life in Huntington was felt Saturday evening. Andrea said Ben is  going to be a typical Southern boy with a big truck with a gun in the back.  Andrew enjoyed all types of music. Andrew Mattison would then make his first of three field goals on a 26-yarder for a 31-14 lead. And rigorous attempts to game out such a conflict suggest that it could be very bad for the U.S. as well. Android Marshmallow is coming your way. And Romney virtually shut the door on any talk of him being drafted into the campaign at a later time. And, she added, GM is prepared to make more trucks if necessary. And she threw in some warm shearling coats worthy of the frigid weather. And so, everyone can talk us here about their credentials. And some, I assume, are good people,” Trump said when he announced his candidacy for presidency in New York City on June 16, 2015. And some of his pronouncements about women sound like they come from the mouth of the 78-year-old Latin American celibate cleric that he is. And some of the latest research seems to say that our genes may be responsible for how we react to coffee, explaining why some of us need several cups to get a boost while others get the jitters on only one. And so with the commercial aspect of these films, the financial wooing and winning of these films, it has made independent viable. And surrendering that power in order to promote a diet book doesn't feel relatable or real; it feels weak.  And that I'm looking forward to. And that is, after all, what auto shows are essentially about. And that I was a supporter of Farage. And that really saved my life in that I was able to go to work at 'Saturday Night Live' and exist through each day while I was figuring this out,  she told NPR. And that's what's always made this so hard. And that’s why communication is more “art” than science. And that unbiased view can be compared to similar data sets in the past and in the future. And the big conservative victories — striking down new EPA rules restricting toxic emissions from power plants and upholding the use of a painful death-penalty drug — are odd ones to celebrate, he said.  And the last three Fridays, they've been closed because they've been catering a banquet,  he said. And the MPD was sticking to policy.  And then even if a clot does happen, alcohol can help with fibrinolysis, which is the dissolution of the clot.  And then there's the most talked-about issue: Internet fast lanes. And then there's the other element. And then there was the corrosive legacy of segregation, Jim Crow and broad-based discrimination that continued long after the end of slavery, and that still overshadows black life in America today. And then, you have to reverse-engineer it and see that those numbers represent something that can't be explained locally. And there's only one German who's ever clinched the award - Reinhard Selten, who impressed the jury with his game theory research back in 1994. And the United States and our coalition partners will stand with them as they do so.   And the United States and our coalition partners will stand with them as they do so.  And they are ready to continue to starve themselves -- and risk their lives -- in order to make sure that happens. And they probably haven't lived on that $40,000-a-year starting salary in a very long time. And they want to shut the doors and they don't want people to make their choices about the future. And this is all supposed to support Syrians and help them regain a decent life. And this transformed my feeling on Earth, from 'Me and Them' to a feeling of 'Us.' And those allegations don't match our core values,  he said. And to celebrate, the website has unveiled a new iOS app that won’t require you to jailbreak your iPhone or iPad to use. And too many still aren’t working at all,  Obama said in last year's State of the Union address  Our job is to reverse these trends. And Trump, whose latest gems included hateful rants about all Mexican immigrants as murderers and criminals and his moronic complaints about high oil prices (oil has gone down by roughly 60 percent since June 2014). And we are absolutely thrilled that Noomi has come unboard, as she is dream casting for Callas. And we are doing so in many different ways, not least by sharing intelligence, working more closely with France in their efforts to fight ISIL,  he said. And we did on New Year's Eve, 2013. And what can be done to enhance WiFi security? And when he participates in something like this at his home, he's saying to the kids, ‘Okay, this is a safe place.’ And when those consequences happen every now and then, do not beat yourself up. And when you fail two to three polygraphs, the percentages are really high. And while many found themselves at the beach for the Air and Water Show or in the bleachers for the Crosstown, we headed to Stage 773 for the Third Annual Nerd Comedy Festival. And while many questioned Trudeau publicly welcoming Adams into the party at a national press conference last winter, Variyan said it was important to recognize the floor-crossing by a junior cabinet minister. And while the humidity is not expected to drop low enough for wildfire warnings, 10 to 20 mph winds and the ongoing drought could pose a threat of fire, according to the L.A. Times. And while the number of white youth jailed has fallen significantly, last year for the first time black kids were a majority of the jail population on any given day. And while you can buy VSCO filters on your phone for a few bucks, these VSCO Film plugins were sold in packs for up to $119 each. And while you do earn five coins for each puzzle you solve, Hints cost 50 coins, so it's hard to earn enough to purchase any meaningful amount of Hints. And why, for example, were abortion services excluded from the programming Canada would help fund? And why wouldn't Saudi Arabia and Turkey have basically supported ISIS, if they thought it served their  national interests  to do so? Andy Borowitz is a New York Times best-selling author and a comedian who has written for The New Yorker since 1998. And yes, they can be embedded on the web should the need arise.";
        RegexFinder rf = new RegexFinder();
        List<Span> timeSpans = rf.filterFind(timeFinder, input);
        for (Span s : timeSpans){
         System.out.println("DEBUGGING:" + s.getStart() + "=--="+s.getEnd());
         System.out.println("DEBUGGING span:" + input.substring(s.getStart(), s.getEnd()));
        LinkedList<String> normalizedStartAndEnd = normalizeEnglishDate(input.substring(s.getStart(), s.getEnd()));;
              System.out.println("DEBUGGING norm:" + normalizedStartAndEnd);;
        }
      }


	public static LinkedList<String> normalizeEnglishDate(String foundDate) {

		// this is directly coupled to the dateRegexMap thing. If that changes, this needs to be checked too
		Date normalizedStartDate = new Date();
		Date normalizedEndDate = new Date();
		Calendar cal = Calendar.getInstance();
		
		Iterator it = englishDateRegexMap.entrySet().iterator();
		LinkedList<String> dates = new LinkedList<String>();
		
		
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			int key = (Integer) pair.getKey();
			String p = (String) pair.getValue();
			// compile with ^ and $ since I want the whole match/no submatches (not sure if this works though, debug!)
			if (Pattern.matches(String.format("^%s$", p), foundDate)){

				//englishDateRegexMap.put("1", String.format("\\b%s %s(,? %s)?\\b", dayNumber, monthName, yearNumber));
				if (key == 1){
					String[] parts = foundDate.split("\\s");
					int dayNumber = Integer.parseInt(parts[0]);
					int monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (parts.length > 2){
						yearNumber = Integer.parseInt(parts[2]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					cal.set(yearNumber, monthNumber-1, dayNumber, 0, 0, 0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				
				//englishDateRegexMap.put(2, String.format("\\b%s( %s(st|nd|rd|th)?)?,?( %s)?\\b", monthName, dayNumber, yearNumber)); // TODO: debug for/also allow superscript for th?
				if (key == 2){
					String[] parts = foundDate.split("\\s");
					int monthNumber = englishMonthName2Number.get(parts[0].toLowerCase().replaceAll("\\p{P}", ""));
					int dayNumber = 1;
					int yearNumber = DateCommons.getYearFromAnchorDate(); // TODO: guess I should use tense here as well. If someone says "in February", guess it depends on the tense of the sentence if last or next feb is meant
					int increaseCalendarUnit = Calendar.DATE;
					if (parts.length > 1){
						if (parts[1].matches("\\d{1,2}(st|nd|rd|th)?")){
							dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", "").replaceAll(",",""));	
						}
						else if (parts[1].matches("\\d{4}")){
							yearNumber = Integer.parseInt(parts[1]);
						}
						else{
							yearNumber = DateCommons.getYearFromAnchorDate();
						}
					}
					else {
						increaseCalendarUnit = Calendar.MONTH;
					}
					//System.out.println("DEBUG:" + yearNumber + "|" + monthNumber + "|" + dayNumber);
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(increaseCalendarUnit, 1, normalizedStartDate); // TODO: this is problematic in case of "January 2017"
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//englishDateRegexMap.put(3, String.format("\\b%s\\b", yearNumber));
				if (key == 3){
					int dayNumber = 1;
					int monthNumber = 1;
					int yearNumber = Integer.parseInt(foundDate);
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(4, String.format("\\b%s[/\\-\\.]%s([/\\-\\.]%s)?\\b", dayNumber, monthNumber, yearNumber));
				if (key == 4){
					String[] parts = foundDate.split("[^\\d]+");
					int dayNumber = Integer.parseInt(parts[0]);
					int monthNumber = Integer.parseInt(parts[1]);
					int yearNumber = 0;
					if (parts.length > 2){
						yearNumber = Integer.parseInt(parts[2]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber, 0, 0, 0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(5, String.format("\\bmid\\-%s\\b", yearNumber));
				if (key == 5){
					String[] parts = foundDate.split("(-| )");
					int yearNumber = Integer.parseInt(parts[1].trim());
					int monthNumber = 6;
					int dayNumber = 1;
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 2, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(6, String.format("\\bmid\\-%s\\b", monthName));
				if (key == 6){
					String[] parts = foundDate.split("(-| )");
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().trim().replaceAll("\\p{P}", ""));
					int dayNumber = 10;
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(7, String.format("\\b%s( of)? %s\\b", season, yearNumber));
				if (key == 7){
					String[] parts = foundDate.split("\\s");
					String season = parts[0];
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (parts.length > 1){
						yearNumber = Integer.parseInt(parts[parts.length-1]);	
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					
					//final String season = "(?i)(summer|winter|spring|autumn|fall)";
					int startDay = 1;
					int startMonth = 0;
					int endDay = 1;
					int endMonth = 0;
					if (season.equalsIgnoreCase("spring")){
						startMonth = 3;
						endDay = 31;
						endMonth = 5;
					}
					else if (season.equalsIgnoreCase("summer")){
						startMonth = 6;
						endDay = 31;
						endMonth = 8;
					}
					else if (season.equalsIgnoreCase("(autumn|fall)")){
						startMonth = 9;
						endDay = 30;
						endMonth = 11;
					}
					else if (season.equalsIgnoreCase("winter")){
						startMonth = 12;
						endDay = 28;
						endMonth = 2;
						if (((GregorianCalendar) cal).isLeapYear(yearNumber)){
							endDay = 29;
						}
					}
					if (season.equalsIgnoreCase("winter")){
						cal.set(yearNumber-1, startMonth-1, startDay,0,0,0);
					}
					else{
						cal.set(yearNumber, startMonth-1, startDay,0,0,0);
					}
					normalizedStartDate = cal.getTime();
					cal.set(yearNumber, endMonth-1, endDay,0,0,0);
					normalizedEndDate = cal.getTime();
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(8, String.format("\\b%s of %s\\b", beginningMiddleEnd, yearNumber));
				if (key == 8){
					String[] parts = foundDate.split("\\s");
					int yearNumber = Integer.parseInt(parts[parts.length-1]);
					int dayNumber = 1;
					int monthNumber = 0;
					//final String beginningMiddleEnd = "(?i)(beginning|middle|end)";
					if (parts[0].matches("(?i)(start|beginning)")){
						monthNumber = 1;
					}
					else if (parts[0].matches("(?i)middle")){
						monthNumber = 5;
					}
					else if (parts[0].matches("(?i)end")){
						monthNumber = 10;
					}
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 3, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//englishDateRegexMap.put(9, String.format("\\b%s of %s\\b", beginningMiddleEnd, monthName));
				if (key == 9){
					String[] parts = foundDate.split("\\s");
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = englishMonthName2Number.get(parts[parts.length-1].toLowerCase().replaceAll("\\p{P}", ""));
					int dayNumber = 1;
					if (parts[0].matches("(?i)(start|beginning)")){ // redundant...
						dayNumber = 1;
					}
					else if (parts[0].matches("(?i)middle")){
						dayNumber = 10;
					}
					else if (parts[0].matches("(?i)end")){
						dayNumber = DateCommons.getLastDayOfMonth(monthNumber, yearNumber) - 10;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(10, String.format("\\bthe %s(st|nd|rd|th)\\b", dayNumber)); // TODO: debug for/also allow superscript for th?
				if (key == 10){
					String[] parts = foundDate.split("\\s");
					int dayNumber = Integer.parseInt(parts[1].replaceAll("(st|nd|rd|th)", ""));
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					if (monthNumber == 0){
						monthNumber += 1;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(11, String.format("\\b%s", beforeChrist));
				if (key == 11){
					String parts[] = foundDate.split("\\s");
					String yearNumberBeforeChrist = parts[0];
					int yearSubtract = cal.get(Calendar.YEAR) + Integer.parseInt(yearNumberBeforeChrist);
					cal.add(Calendar.YEAR, -yearSubtract);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate)); // start date
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate)); // end date
					DateCommons.updateAnchorDate(normalizedEndDate);
				}
				
				//englishDateRegexMap.put(12, String.format("\\b(early|late) %s\\b", yearNumber));
				if (key == 12){
					String[] parts = foundDate.split("\\s");
					int yearNumber = Integer.parseInt(parts[1]);
					int monthNumber = 0;
					int dayNumber = 1;
					if (parts[0].toLowerCase().matches("early")){
						monthNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("late")){
						monthNumber = 10;
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 3, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				
				//englishDateRegexMap.put(13, String.format("\\b(early|late) %s\\b", monthName));
				if (key == 13){
					String[] parts = foundDate.split("\\s");
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = englishMonthName2Number.get(parts[1].toLowerCase().replaceAll("\\p{P}", ""));
					int dayNumber = 1;
					if (parts[0].toLowerCase().matches("early")){
						dayNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("late")){
						dayNumber = DateCommons.getLastDayOfMonth(monthNumber, yearNumber) - 10; 
					}
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(14, String.format("\\b(today|now|this day)\\b"));
				if (key == 14){
					// Just taking the current time here. Think about/discuss what to do here. Ideally, this should be time of writing the document. But that's not available in all cases.
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					String formatted = sdf.format(cal.getTime());
					normalizedStartDate = DateCommons.normParseDate(formatted);
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(15, String.format("(?i)\\b%s (%s|%s|%s|%s) (later|after|earlier|before)\\b", alphaNumber, day, week, month, year));
				if (key == 15){
					String[] parts = foundDate.split("\\s");
					int direction = 0;
					if (parts[2].toLowerCase().matches("(later|after)")){
						direction = 1;
					}
					else if (parts[2].toLowerCase().matches("(earlier|before)")){
						direction = -1;
					}
					int increment = convertEnglishAlphaNumber(parts[0].toLowerCase()) * direction;
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					if (monthNumber == 0){
						monthNumber += 1;
					}
					int dayNumber = DateCommons.getDayFromAnchorDate();
					cal.set(yearNumber,  monthNumber-1, dayNumber,0,0,0);
					Date anchor = cal.getTime();
					//final String day = "(?i)days?";
					//final String week = "(?i)weeks?";
					//final String month = "(?i)months?";
					//final String year = "(?i)years?";
					if (parts[1].toLowerCase().matches("(?i)days?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, increment, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					}
					else if (parts[1].toLowerCase().matches("(?i)weeks?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, increment * 7, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1 * 7, normalizedStartDate);
					}
					else if (parts[1].toLowerCase().matches("(?i)months?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.MONTH, increment, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.MONTH, 1, normalizedStartDate);
					}
					else if (parts[1].toLowerCase().matches("(?i)years?")){
						normalizedStartDate = DateCommons.increaseCalendar(Calendar.YEAR, increment, anchor);
						normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 1, normalizedStartDate);
					}
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(16, String.format("\\b%ss\\b", yearNumber));
				if (key == 16){
					int yearNumber = Integer.parseInt(foundDate.replace("s", ""));
					int monthNumber = 1;
					int dayNumber = 1;
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.YEAR, 10, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(17, String.format("\\b%s\\b", dayName));
				if (key == 17){
					//TODO: fix this once I have sentence tense in place. The approach below is a simplified version, and should really be properly debugged before using it.
					
					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					cal.set(yearNumber,  monthNumber, dayNumber,0,0,0);
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
					int daysOfIncrease = 0;
					int x = englishDayName2Integer.get(foundDate.trim().toLowerCase());
					if (x > dayOfWeek){
						// it is in the rest of the week
						daysOfIncrease = x - dayOfWeek;
					}
					else if (x == dayOfWeek){
						// do nothing, it is the current day
						daysOfIncrease = 0;
					}
					else{ // else x < currentDayOfWeek and the day is in the next week
						daysOfIncrease = 7 - dayOfWeek + x;
					}
					normalizedStartDate = DateCommons.increaseCalendar(Calendar.DATE, daysOfIncrease, cal.getTime());
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
					
				}
				
				//final String holidays = "(?i)(christmas( day)?|easter( monday)?|new year'?s day|may day|boxing day|independence day|labor day|memorial day|veterans day|thanksgiving)";
				//englishDateRegexMap.put(18, String.format("\\b%s( %s)?\\b", holidays, yearNumber));
				if (key == 18){
					int yearNumber = DateCommons.getYearFromAnchorDate();
					String[] parts = foundDate.split("\\s");
					if (parts[parts.length-1].matches("([1-2]\\d{3})")){
						yearNumber = Integer.parseInt(foundDate.split("\\s")[foundDate.split("\\s").length-1]);
					}
					else{
						yearNumber = DateCommons.getYearFromAnchorDate();
					}
					int monthNumber = 1;
					int dayNumber = 1;
					if (parts[0].toLowerCase().matches("christmas")){
						monthNumber = 12;
						dayNumber = 25;
					}
					else if (parts[0].toLowerCase().matches("boxing")){
						monthNumber = 12;
						dayNumber = 26;
					}
					else if (parts[0].toLowerCase().matches("thanksgiving")){
						monthNumber =  07;
						dayNumber = 04;
					}
					else if (parts[0].toLowerCase().matches("veteran'?s")){
						monthNumber = 11 ;
						dayNumber = 11;
					}
					else if (parts[0].toLowerCase().matches("new")){
						monthNumber = 1;
						dayNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("may")){
						monthNumber = 5;
						dayNumber = 1;
					}
					else if (parts[0].toLowerCase().matches("labor")){
						monthNumber = 9;
						dayNumber = DateCommons.getFirstSpecificDayOfMonth(yearNumber, monthNumber, "monday");
					}
					else if (parts[0].toLowerCase().matches("memorial")){
						monthNumber = 5;
						dayNumber = DateCommons.getLastSpecificDayOfMonth(yearNumber, monthNumber, "monday");
					}
					else if (parts[0].toLowerCase().matches("easter")){
						Calendar loCal = Calendar.getInstance();
						loCal.setTime(DateCommons.getEasterDate(yearNumber));
						monthNumber = loCal.get(Calendar.MONTH);
						dayNumber = loCal.get(Calendar.DATE);
					}
					//TODO: include pentecost, ascension, etc.
					
					cal.set(yearNumber, monthNumber-1, dayNumber,0,0,0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.DATE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
				
				//englishDateRegexMap.put(19, String.format("\\b%s (?i)(a.m|p.m)\\b", time));
				if (key == 19){

					int dayNumber = DateCommons.getDayFromAnchorDate();
					int monthNumber = DateCommons.getMonthFromAnchorDate();
					int yearNumber = DateCommons.getYearFromAnchorDate();
					int hour = 0;
					int minute = 0;
					String[] parts = foundDate.split("(:|\\.|\\s)");
					if(parts[0].matches("([0-9]|0[0-9]|1[0-1])")){
						hour = Integer.parseInt(parts[0]);
					}
					if(parts[1].matches("[0-5][0-9]")){
						minute = Integer.parseInt(parts[1]);
					}
					if (parts[2].matches("p")){
						hour = hour+12;
					}else{
						//do nothing
					}
					cal.set(yearNumber,  monthNumber, dayNumber, hour, minute, 0);
					normalizedStartDate = cal.getTime();
					normalizedEndDate = DateCommons.increaseCalendar(Calendar.MINUTE, 1, normalizedStartDate);
					dates.add(DateCommons.fullDateFormat.format(normalizedStartDate));
					dates.add(DateCommons.fullDateFormat.format(normalizedEndDate));
					DateCommons.updateAnchorDate(normalizedStartDate);
				}
			}

		}
		
		return dates;
	}

	


	private static Integer convertEnglishAlphaNumber(String alphaNumber){
		
		HashMap<String,Integer> m = new HashMap<String,Integer>();
		m.put("one",1);
		m.put("two",2);
		m.put("three",3);
		m.put("four",4);
		m.put("five",5);
		m.put("six",6);
		m.put("seven",7);
		m.put("eight",8);
		m.put("nine",9);
		m.put("ten",10);
		m.put("eleven",11);
		m.put("twelve",12);
		m.put("thirteen",13);
		m.put("fourteen",14);
		m.put("fifteen",15);
		m.put("sixteen",16);
		m.put("seventeen",17);
		m.put("eighteen",18);
		m.put("nineteen",19);
		m.put("twenty",20);
		m.put("twenty-one",21);
		m.put("twenty-two",22);
		m.put("twenty-three",23);
		m.put("twenty-four",24);
		m.put("twenty-five",25);
		m.put("twenty-six",26);
		m.put("twenty-seven",27);
		m.put("twenty-eight",28);
		m.put("twenty-nine",29);
		m.put("thirty",30);
		m.put("thirty-one",31);
		m.put("thirty-two",32);
		m.put("thirty-three",33);
		m.put("thirty-four",34);
		m.put("thirty-five",35);
		m.put("thirty-six",36);
		m.put("thirty-seven",37);
		m.put("thirty-eight",38);
		m.put("thirty-nine",39);
		m.put("forty",40);
		m.put("forty-one",41);
		m.put("forty-two",42);
		m.put("forty-three",43);
		m.put("forty-four",44);
		m.put("forty-five",45);
		m.put("forty-six",46);
		m.put("forty-seven",47);
		m.put("forty-eight",48);
		m.put("forty-nine",49);
		m.put("fifty",50);
		m.put("fifty-one",51);
		m.put("fifty-two",52);
		m.put("fifty-three",53);
		m.put("fifty-four",54);
		m.put("fifty-five",55);
		m.put("fifty-six",56);
		m.put("fifty-seven",57);
		m.put("fifty-eight",58);
		m.put("fifty-nine",59);
		m.put("sixty",60);
		m.put("sixty-one",61);
		m.put("sixty-two",62);
		m.put("sixty-three",63);
		m.put("sixty-four",64);
		m.put("sixty-five",65);
		m.put("sixty-six",66);
		m.put("sixty-seven",67);
		m.put("sixty-eight",68);
		m.put("sixty-nine",69);
		m.put("seventy",70);
		m.put("seventy-one",71);
		m.put("seventy-two",72);
		m.put("seventy-three",73);
		m.put("seventy-four",74);
		m.put("seventy-five",75);
		m.put("seventy-six",76);
		m.put("seventy-seven",77);
		m.put("seventy-eight",78);
		m.put("seventy-nine",79);
		m.put("eighty",80);
		m.put("eighty-one",81);
		m.put("eighty-two",82);
		m.put("eighty-three",83);
		m.put("eighty-four",84);
		m.put("eighty-five",85);
		m.put("eighty-six",86);
		m.put("eighty-seven",87);
		m.put("eighty-eight",88);
		m.put("eighty-nine",89);
		m.put("ninety",90);
		m.put("ninety-one",91);
		m.put("ninety-two",92);
		m.put("ninety-three",93);
		m.put("ninety-four",94);
		m.put("ninety-five",95);
		m.put("ninety-six",96);
		m.put("ninety-seven",97);
		m.put("ninety-eight",98);
		m.put("ninety-nine",99);
		

		int r = 0;
		if (alphaNumber.matches("(one )?hundred")){
			r = 100;
		}
		else{
			r = m.get(alphaNumber.toLowerCase());
		}
		
		return r;
	}


}
